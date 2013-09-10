package org.mariella.oxygen.spring;


import javax.persistence.EntityTransaction;

import org.mariella.oxygen.runtime.impl.OxyEntityTransactionFactory;
import org.mariella.oxygen.runtime.impl.OxyServerEntityManagerImpl;

public class OxyDataSourceTransactionManagerManagedTransactionFactory implements OxyEntityTransactionFactory {

public EntityTransaction createEntityTransaction(OxyServerEntityManagerImpl entityManagerImpl) {
	return new OxyDataSourceTransactionManagerManagedTransaction(entityManagerImpl);
}

}
