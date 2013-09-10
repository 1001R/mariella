package org.mariella.oxygen.runtime.core;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.persistence.loader.ModifiableFactory;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.persistor.DatabaseAccess;

/**
 * @author aim
 *
 */
public interface OxyServerEntityManager extends EntityManager, OxyContext, OxyEntityManager {

/* (non-Javadoc)
 * @see org.mariella.oxygen.modifications.Context#getSchemaRepresentation()
 */
public SchemaMapping getSchemaMapping();


public DatabaseAccess createDatabaseAccess();

/**
 * Clear the persistence context, causing all managed entities to become detached.
 */
public void clear();

/**
 * Close an application-managed EntityManager.
 */
public void close();

/**
 * Find by primary key.
 */
public <T> T find(Class<T> arg0, Object arg1);

/**
 * Synchronize the persistence context to the underlying database.
 */
public void flush();

/**
 * Get the flush mode that applies to all objects contained in the persistence context.
 */
public FlushModeType getFlushMode();

/**
 *  Returns the resource-level transaction object.
 */
public EntityTransaction getTransaction();

/**
 * Determine whether the EntityManager is open.
 */
public boolean isOpen();

/**
 * Set the lock mode for an entity object contained in the persistence context.
 */
public void lock(Object entity, LockModeType lockMode);

/**
 * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
 */
public void refresh(Object entity);

/**
 *  Set the flush mode that applies to all objects contained in the persistence context.
 */
public void setFlushMode(FlushModeType flushMode);

public ModifiableFactory getModifiableFactory();
}