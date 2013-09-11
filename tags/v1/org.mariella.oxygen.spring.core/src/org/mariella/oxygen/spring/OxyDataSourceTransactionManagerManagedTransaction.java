package org.mariella.oxygen.spring;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

import org.mariella.oxygen.runtime.impl.OxyServerEntityManagerImpl;

public class OxyDataSourceTransactionManagerManagedTransaction implements EntityTransaction {

private final OxyServerEntityManagerImpl entityManager;
private boolean active = false;
private boolean rollbackOnly = false;

public OxyDataSourceTransactionManagerManagedTransaction(OxyServerEntityManagerImpl entityManager) {
	this.entityManager = entityManager;
}
public void begin() {
	if(isActive()) {
		throw new IllegalStateException("EntityTransaction is already active!");
	} else {
		active = true;
	}
}

public void commit() {
	try {
		entityManager.flush();
	} catch(Exception e) {
		throw new RollbackException(e);
	}
}

public boolean getRollbackOnly() {
	if(!isActive()) {
		throw new IllegalStateException("EntityTransaction is not active!");
	}
	return rollbackOnly;
}

public boolean isActive() {
	return active;
}

public void rollback() {
	if(!isActive()) {
		throw new IllegalStateException("EntityTransaction is not active!");
	}
}

public void setRollbackOnly() {
	if(!isActive()) {
		throw new IllegalStateException("EntityTransaction is not active!");
	}
	rollbackOnly = true;
}

public EntityManager getEntityManager() {
	return entityManager;
}

}
