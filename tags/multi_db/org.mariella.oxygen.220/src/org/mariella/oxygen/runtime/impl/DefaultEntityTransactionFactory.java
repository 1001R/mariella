package org.mariella.oxygen.runtime.impl;

import javax.persistence.EntityTransaction;

public class DefaultEntityTransactionFactory implements OxyEntityTransactionFactory {

public EntityTransaction createEntityTransaction(OxyServerEntityManagerImpl entityManagerImpl) {
	return new EntityTransactionImpl(entityManagerImpl);
}

}
