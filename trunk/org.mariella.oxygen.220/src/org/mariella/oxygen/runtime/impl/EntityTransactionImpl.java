package org.mariella.oxygen.runtime.impl;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

/**
 * @author aim
 */
public class EntityTransactionImpl implements EntityTransaction {
	private final OxyServerEntityManagerImpl entityManager;
	private boolean active = false;
	private boolean rollbackOnly = false;

public EntityTransactionImpl(OxyServerEntityManagerImpl entityManager) {
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
		entityManager.getConnection().commit();
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
	try {
		entityManager.getConnection().rollback();
	} catch(Exception e) {
		throw new PersistenceException(e);		
	}
}

public void setRollbackOnly() {
	if(!isActive()) {
		throw new IllegalStateException("EntityTransaction is not active!");
	}
	rollbackOnly = true;
}


}
