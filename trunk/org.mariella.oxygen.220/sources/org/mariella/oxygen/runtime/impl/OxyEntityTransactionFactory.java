package org.mariella.oxygen.runtime.impl;

import javax.persistence.EntityTransaction;

public interface OxyEntityTransactionFactory {

public EntityTransaction createEntityTransaction(OxyServerEntityManagerImpl entityManagerImpl);

}
