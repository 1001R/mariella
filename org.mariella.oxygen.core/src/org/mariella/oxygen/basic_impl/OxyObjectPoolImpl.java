package org.mariella.oxygen.basic_impl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.runtime.ModificationInfo.Status;
import org.mariella.persistence.runtime.ModificationTrackerEntityListener;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.util.Assert;

public class OxyObjectPoolImpl implements OxyObjectPool, Serializable {
	private static final long serialVersionUID = 1L;

	private class EntityListener implements ModificationTrackerEntityListener, Serializable {
		private static final long serialVersionUID = 1L;

		public void participantRemoved(Object participant, ModificationInfo info) {
		}

		public void participantFlushed(Object participant, ModificationInfo info) {
			OxyObjectPoolImpl.this.participantFlushed(participant);
		}
		public void participantAdded(Object participant, ModificationInfo info) {
			OxyObjectPoolImpl.this.participantAdded(participant);
		}
	}

	protected transient OxyEntityManagerImpl entityManager;

	private EntityManagerModificationTracker modificationTracker;
	private Map<String, Object> attributes;
	private String info;

	private Map<Object, EntityState> entityStates = new HashMap<Object, EntityState>();
	private Map<Long, EntityState> poolIdentityMap = new HashMap<Long, EntityState>();

	private long nextPoolIdentity = 0;
	private long nextFlushTimestamp = 0;
	private EntityListener entityListener = new EntityListener();

	private boolean merging = false;

public OxyObjectPoolImpl(EntityManagerModificationTracker modificationTracker) {
	super();
	this.modificationTracker = modificationTracker;
	modificationTracker.addEntityListener(entityListener);
}

public EntityManagerModificationTracker getModificationTracker() {
	return modificationTracker;
}

public OxyEntityManagerImpl getEntityManager() {
	return entityManager;
}

public Object getAttribute(String attributeName) {
	return attributes == null ? null : attributes.get(attributeName);
}

public void setAttribute(String attributeName, Object value) {
	if(attributes == null) {
		attributes = new HashMap<String, Object>();
	}
	attributes.put(attributeName, value);
}

public void setEntityManager(OxyEntityManagerImpl entityManager) {
	this.entityManager = entityManager;
	modificationTracker.setEntityManager(entityManager);
}

public Collection<EntityState> getAllEntityStates() {
	return entityStates.values();
}

public Object getEntityForPersistentId(Object identity) {
	return modificationTracker.getParticipant(identity);
}

public EntityState getEntityStateForPoolId(long poolId) {
	return poolIdentityMap.get(poolId);
}

public EntityState getEntityState(Object entity) {
	return entityStates.get(entity);
}

public long getPoolIdForEntity(Object entity) {
	return getEntityState(entity).getPoolIdentity();
}

public boolean contains(Object entity) {
	return modificationTracker.getParticipants().contains(entity);
}

public boolean isPersistentIdRemoved(Object identity) {
	Object entity = getEntityForPersistentId(identity);
	ModificationInfo info = modificationTracker.getModificationInfo(entity);
	return info == null ? false : info.getStatus() == Status.Removed || info.getStatus() == Status.NewRemoved;
}

public Object getPersistentIdentityForEntity(Object entity) {
	return modificationTracker.getIdentity(entity);
}

private void participantAdded(Object entity) {
	if(!merging) {
		createEntityState(entity);
	}
}

private void participantFlushed(Object entity) {
	getEntityState(entity).flushed();
}

public RuntimeException createRuntimeException(Throwable t) {
	return entityManager.createRuntimeException(t);
}

public boolean containsPoolIdentity(long poolIdentity) {
	return poolIdentityMap.containsKey(poolIdentity);
}

public void mergeRelated(OxyObjectPool sourcePool) throws Exception  {
	merging = true;
	getModificationTracker().setEnabled(false);
	try {
		MergeContext mergeContext = new RelatedMergeMergeContext(this, sourcePool);

		for(EntityState myState : new ArrayList<EntityState>(getAllEntityStates())) {
			if(myState.isRemoved() && sourcePool.getEntityStateForPoolId(myState.getPoolIdentity()) == null) {
				getModificationTracker().primitiveRemoveExisitingParticipant(myState.getEntity());
				entityStates.remove(myState.getEntity());
			}
		}

		// register all not registered identities
		nextPoolIdentity = ((OxyObjectPoolImpl)sourcePool).nextPoolIdentity;
		for(EntityState sourceState : sourcePool.getAllEntityStates()) {
			EntityState myState = getEntityStateForPoolId(sourceState.getPoolIdentity());
			if(myState != null) {
				if(myState.isNew() && !sourceState.isNew()) {
					getModificationTracker().primitiveAddExistingParticipant(myState.getEntity());
				}
			} else {
				if(!sourceState.isRemoved()) {
					ClassDescription cd = entityManager.getClassDescription(entityManager.getClass(sourceState.getClassDescription()));
					Object entity = createEntity(cd);
					createEntityState(entity, sourceState.getPoolIdentity());
				}
			}
		}

		// merge
		for(EntityState sourceState : sourcePool.getAllEntityStates()) {
			EntityState myState = getEntityStateForPoolId(sourceState.getPoolIdentity());
			myState.merge(mergeContext, sourceState);
			Object entity = myState.getEntity();
			if(sourceState.isNew()) {
				getModificationTracker().addNewParticipant(entity);
			} else if (!getModificationTracker().isParticipant(entity)){
				getModificationTracker().addExistingParticipant(entity);
			}
		}
	} finally {
		getModificationTracker().setEnabled(true);
		merging = false;
	}
}

public Object mergeEntity(OxyObjectPool sourcePool, Object sourceEntity) {
	if(sourceEntity == null) {
		return null;
	} else {
		UnrelatedMergeContext mergeContext = new UnrelatedMergeContext(this, sourcePool);
		return mergeContext.getMyEntity(sourceEntity);
	}
}

public long newFlushTimestamp() {
	return nextFlushTimestamp++;
}


public static void assertionFailed(String reason) {
	throw new RuntimeException(reason);
}

public Object createEntity(ClassDescription classDescription) {
	try {
//		Class<?> clazz = entityManager.getEntityClassLoader().loadClass(classDescription.getClassName());
		Class<?> clazz = entityManager.getEntityClassResolver().resolveClass(classDescription.getClassName());
		return clazz.newInstance();
	} catch(ClassNotFoundException e) {
		throw new RuntimeException(e);
	} catch(IllegalAccessException e) {
		throw new RuntimeException(e);
	} catch(InstantiationException e) {
		throw new RuntimeException(e);
	}
}

private EntityState createEntityState(Object entity, long poolIdentity) {
	Assert.assertTrue(poolIdentityMap.get(poolIdentity) == null);
	EntityState state = new EntityState(this, poolIdentity, entity);
	poolIdentityMap.put(state.getPoolIdentity(), state);
	entityStates.put(entity, state);
	return state;
}

public EntityState createEntityState(Object entity) {
	return createEntityState(entity, nextPoolIdentity++);
}

public String getInfo() {
	return info;
}

public void setInfo(String info) {
	this.info = info;
}

@Override
public String toString() {
	return super.toString() + " (" + info + ")";
}

}
