package org.mariella.oxygen.runtime.impl;


import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.basic_impl.OxyEntityManagerImpl;
import org.mariella.oxygen.runtime.core.OxyConnectionProvider;
import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.persistence.database.ConnectionCallback;
import org.mariella.persistence.loader.ModifiableFactory;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.persistor.DatabaseAccess;
import org.mariella.persistence.persistor.Persistor;

/**
 * @author aim
 */
public class OxyServerEntityManagerImpl extends OxyEntityManagerImpl implements OxyServerEntityManager {
	private final OxyConnectionProvider connectionProvider;

	protected final SchemaMapping schemaMapping;
	private EntityTransaction entityTransaction;
	private ModifiableFactory modifiableFactory;

	public boolean open = true;


public OxyServerEntityManagerImpl(OxyConnectionProvider connectionProvider, SchemaMapping schemaMapping, OxyEntityTransactionFactory transactionFactory, ClassResolver classResolver) {
	super(classResolver, schemaMapping.getSchemaDescription());
	this.schemaMapping = schemaMapping;
	this.connectionProvider = connectionProvider;
	this.entityTransaction = transactionFactory.createEntityTransaction(this);
//	modifiableFactory = new ModifiableFactoryImpl(entityClassLoader);
	modifiableFactory = new OxyModifiableFactory(classResolver);
}

public ModifiableFactory getModifiableFactory() {
	return modifiableFactory;
}

public Connection getConnection() {
	return connectionProvider.getConnection();
}

public boolean contains(Object entity) {
	return objectPool.contains(entity);
}

public void clear() {
	throw new UnsupportedOperationException();
}

public void close() {
	open = false;
	connectionProvider.close();
}

public Query createNamedQuery(String name) {
	throw new UnsupportedOperationException();
}

public Query createNativeQuery(String sqlString, Class resultClass) {
	throw new UnsupportedOperationException();
}

public Query createNativeQuery(String sqlString, String resultSetMappingName) {
	throw new UnsupportedOperationException();
}

public Query createNativeQuery(String sqlString) {
	throw new UnsupportedOperationException();
}

public Query createQuery(String sqlString) {
	throw new UnsupportedOperationException();
}
public <T> T find(Class<T> arg0, Object arg1) {
	throw new UnsupportedOperationException();
}

public void checkTransaction() throws TransactionRequiredException {
	if(!getTransaction().isActive()) {
		throw new TransactionRequiredException("There is no active transaction!");
	}
}

public DatabaseAccess createDatabaseAccess() {
	return new DatabaseAccess() {
		@Override
		public Object doInConnection(ConnectionCallback callback) throws SQLException {
			return callback.doInConnection(connectionProvider.getConnection());
		}
	};
}

public void flush() throws IllegalStateException, TransactionRequiredException, PersistenceException {
	try {
		Persistor persistor = new Persistor(schemaMapping, createDatabaseAccess(), objectPool.getModificationTracker());
		persistor.persist();
	} catch(TransactionRequiredException e) {
		throw e;
	} catch(PersistenceException e) {
		throw e;
	} catch(Exception e) {
		throw new PersistenceException(e);
	}
}

public Object getDelegate() {
	return null;
}

public FlushModeType getFlushMode() {
	return FlushModeType.COMMIT;
}

@SuppressWarnings("unchecked")
public <T> T getReference(Class<T> entityClass, Object primaryKey) {
	T entity = (T)objectPool.getEntityForPersistentId(primaryKey);
	if(entity != null) {
		return entity;
	} else {
		throw new UnsupportedOperationException();
	}
}

public EntityTransaction getTransaction() {
	return entityTransaction;
}

public boolean isOpen() {
	return entityTransaction.isActive();
}

public void joinTransaction() {
	throw new UnsupportedOperationException();
}

public void lock(Object entity, LockModeType lockMode) {
	throw new UnsupportedOperationException();
}

public <T> T merge(T entity) {
	throw new UnsupportedOperationException();
}

/**
 * Make an entity instance managed and persistent.
 */
public void persist(Object entity) {
	throw new UnsupportedOperationException();
}

public void refresh(Object entity) {
	throw new UnsupportedOperationException();
}

/**
 *  Remove the entity instance.
 */
public void remove(Object entity) {
	getObjectPool().getModificationTracker().remove(entity);
}

public void setFlushMode(FlushModeType flushMode) {
	throw new UnsupportedOperationException();
}

public RuntimeException createRuntimeException(Throwable t) {
	return new PersistenceException(t);
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}



}
