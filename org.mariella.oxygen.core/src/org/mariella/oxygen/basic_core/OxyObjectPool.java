package org.mariella.oxygen.basic_core;

import java.io.Serializable;
import java.util.Collection;

import org.mariella.persistence.runtime.ModificationTracker;

public interface OxyObjectPool extends ObjectPool, Serializable {

ModificationTracker getModificationTracker();

OxyEntityManager getEntityManager();

Collection<EntityState> getAllEntityStates();

boolean contains(Object entity);

boolean containsPoolIdentity(long poolIdentity);

EntityState getEntityState(Object entity);
EntityState getEntityStateForPoolId(long poolId);

Object getEntityForPersistentId(Object persistentIdentity);
Object getPersistentIdentityForEntity(Object entity);

RuntimeException createRuntimeException(Throwable t);

void mergeRelated(OxyObjectPool sourcePool) throws Exception;
Object mergeEntity(OxyObjectPool sourcePool, Object sourceEntity);

Object getAttribute(String attributeName);
void setAttribute(String attributeName, Object value);

}
