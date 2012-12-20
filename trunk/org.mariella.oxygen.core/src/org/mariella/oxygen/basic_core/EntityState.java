package org.mariella.oxygen.basic_core;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.mariella.oxygen.basic_impl.MergeContext;
import org.mariella.oxygen.basic_impl.OxyObjectPoolImpl;
import org.mariella.persistence.runtime.CollectionModificationInfo;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.runtime.ModificationInfo.Status;
import org.mariella.persistence.runtime.PersistenceException;
import org.mariella.persistence.runtime.TrackedList;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.CollectionPropertyDescription;
import org.mariella.persistence.schema.PropertyDescription;
import org.mariella.persistence.schema.ReferencePropertyDescription;
import org.mariella.persistence.schema.ScalarPropertyDescription;

public class EntityState implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long poolIdentity;
	private Object entity;
	private OxyObjectPoolImpl objectPool;
	private long flushTimestamp;
	
public EntityState(OxyObjectPoolImpl objectPool, long poolIdentity, Object entity) {
	super();
	this.poolIdentity = poolIdentity;
	this.objectPool = objectPool;
	this.entity = entity;
	this.flushTimestamp = objectPool.newFlushTimestamp();
}
	
public long getPoolIdentity() {
	return poolIdentity;
}

public void setPoolIdentity(long poolIdentity) {
	this.poolIdentity = poolIdentity;
}

public OxyObjectPool getObjectPool() {
	return objectPool;
}

public Object getPersistentIdentity() {
	return objectPool.getModificationTracker().getIdentity(entity);
}

public ClassDescription getClassDescription() {
	return objectPool.getEntityManager().getSchemaDescription().getClassDescription(entity.getClass().getName());
}

public Object getEntity() {
	return entity;
}

public long getFlushTimestamp() {
	return flushTimestamp;
}

public void flushed() {
	flushTimestamp = objectPool.newFlushTimestamp();
}

public boolean isNew() {
	ModificationInfo info = objectPool.getModificationTracker().getModificationInfo(entity);
	return info == null ? false : info.getStatus() == Status.New;
}

public boolean isRemoved() {
	ModificationInfo info = objectPool.getModificationTracker().getModificationInfo(entity);
	return info == null ? false : info.getStatus() == Status.NewRemoved || info.getStatus() == Status.Removed;
}

public boolean isDirty() {
	ModificationInfo info = objectPool.getModificationTracker().getModificationInfo(entity);
	return info != null;
}

public ModificationInfo getModificationInfo() {
	return objectPool.getModificationTracker().getModificationInfo(entity);
}

@SuppressWarnings("unchecked")
public void merge(final MergeContext mergeContext, EntityState sourceState) {
	Object sourceEntity = sourceState.getEntity();

	boolean overwrite = mergeContext.isOverwrite(this, sourceState);

	ModificationInfo sourceModificationInfo = mergeContext.getSourcePool().getModificationTracker().getModificationInfo(sourceEntity);
	Collection<String> sourceModifiedProperties = sourceModificationInfo == null ? new HashSet<String>() : sourceModificationInfo.getModifiedProperties();
	
	ModificationInfo modificationInfo = objectPool.getModificationTracker().getModificationInfo(entity);
	boolean modificationInfoAdded = false;
	if(modificationInfo == null) {
		modificationInfo = new ModificationInfo(entity);
		modificationInfo.setStatus(sourceModificationInfo == null ? null : sourceModificationInfo.getStatus());
		modificationInfoAdded = true;
	} else if(overwrite) {
		modificationInfo.setStatus(sourceModificationInfo == null ? null : sourceModificationInfo.getStatus());
	}
	
	for(PropertyDescription pd : getClassDescription().getPropertyDescriptions()) {
		String propertyName = pd.getPropertyDescriptor().getName();
		if(!modificationInfo.isModified(propertyName) || overwrite) {
			if(pd instanceof CollectionPropertyDescription) {
				TrackedList<?> sourceList =(TrackedList<?>)ModifiableAccessor.Singleton.getValue(sourceEntity, pd);
				if(sourceList != null) {
					TrackedList<?>myList = (TrackedList<?>)ModifiableAccessor.Singleton.getValue(entity, pd);
					myList.primitiveClear();
					myList.addAll(mergeCollection(mergeContext, sourceList));
				}
				if(sourceState.getModificationInfo() != null) {
					CollectionModificationInfo cmi = sourceState.getModificationInfo().getCollectionModificationInfo(propertyName);
					getModificationInfo().getCollectionModificationInfo(propertyName).getAdded().addAll(mergeCollection(mergeContext, cmi.getAdded())); 
					getModificationInfo().getCollectionModificationInfo(propertyName).getRemoved().addAll(mergeCollection(mergeContext, cmi.getRemoved()));
				}
			} else if(pd instanceof ScalarPropertyDescription) {
				Object value = ModifiableAccessor.Singleton.getValue(sourceEntity, pd);
				ModifiableAccessor.Singleton.setValue(entity, pd, value);
			} else if(pd instanceof ReferencePropertyDescription || overwrite) {
				Object value = ModifiableAccessor.Singleton.getValue(sourceEntity, pd);
				if(value != null) {
					try {
						value = mergeContext.getMyEntity(value);
					} catch(IllegalStateException e) {
						throw new PersistenceException("Error during merge; Source Entity: " + sourceEntity + "; property: " + pd.getPropertyDescriptor().getName()
								, e);
					}
				}
				ModifiableAccessor.Singleton.setValue(entity, pd, value);
			}

			if(modificationInfo.isModified(propertyName) && !sourceModifiedProperties.contains(propertyName) ) {
				modificationInfo.unmarkModified(propertyName);
			} else if(sourceModifiedProperties.contains(propertyName) && !modificationInfo.isModified(propertyName)) {
				modificationInfo.markModified(propertyName);
			}
		}
	}
	
	if(modificationInfo.getModifiedProperties().isEmpty() && !modificationInfoAdded) {
		mergeContext.getMyPool().getModificationTracker().removeModificationInfo(modificationInfo);
	} else if(!modificationInfo.getModifiedProperties().isEmpty() && modificationInfoAdded) {
		mergeContext.getMyPool().getModificationTracker().addModificationInfo(modificationInfo);
	}
	
	if (entity instanceof CustomMergable) {
		((CustomMergable)entity).mergeWithSource(sourceEntity, new CustomMergeContext() {
			public Object getCustomContext() {
				return mergeContext.getCustomContext();
			}

			public OxyObjectPool getMyPool() {
				return mergeContext.getMyPool();
			}

			public OxyObjectPool getSourcePool() {
				return mergeContext.getSourcePool();
			}
		});
	}
}

@SuppressWarnings("unchecked")
private List mergeCollection(MergeContext mergeContext, Collection sourceCollection) {
	if(sourceCollection != null) {
		List result = new ArrayList();
		for(Object  sourceElement : sourceCollection) {
			Object myElement =  mergeContext.getMyEntity(sourceElement);
			if(!result.contains(myElement)) {
				result.add(myElement);
			}
		}
		return result;
	} else {
		return null;
	}
}



}
