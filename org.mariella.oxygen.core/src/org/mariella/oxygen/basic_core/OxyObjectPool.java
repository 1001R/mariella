package org.mariella.oxygen.basic_core;

import java.io.Serializable;
import java.util.Collection;

import org.mariella.persistence.runtime.ModificationTracker;

public interface OxyObjectPool extends ObjectPool, Serializable {

public ModificationTracker getModificationTracker();

public OxyEntityManager getEntityManager();

public Collection<EntityState> getAllEntityStates();

public boolean contains(Object entity);

public boolean containsPoolIdentity(long poolIdentity);

public EntityState getEntityState(Object entity);
public EntityState getEntityStateForPoolId(long poolId);

public Object getEntityForPersistentId(Object persistentIdentity);
public Object getPersistentIdentityForEntity(Object entity);

public RuntimeException createRuntimeException(Throwable t);

public void mergeRelated(OxyObjectPool sourcePool);
public void mergeRelated(OxyObjectPool sourcePool, Object customContext);
public void merge(OxyObjectPool sourcePool);
public Object mergeEntity(OxyObjectPool sourcePool, Object sourceEntity);

public Object getAttribute(String attributeName);
public void setAttribute(String attributeName, Object value);

public void detachAll();

}
