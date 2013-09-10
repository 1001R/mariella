package org.mariella.oxygen.spring;


import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class OxyDataSourceEntityManagerProvider implements OxyEntityManagerProvider{

private DataSource dataSource;


public OxyServerEntityManager getEntityManager() {
	OxyDataSourceEntityManagerConnectionHolder connHolder = (OxyDataSourceEntityManagerConnectionHolder)TransactionSynchronizationManager.getResource(dataSource);
	if (connHolder == null)
		throw new PersistenceException("Could not fetch OxyEntityManager for dataSource " + dataSource + ". Hint: There must be an active transaction with transactionManager of type OxyDataSourceTransactionManager");
	OxyServerEntityManager entityManager = connHolder.entityManager;
	return entityManager;
}


public void setDataSource(DataSource dataSource) {
	this.dataSource = dataSource;
}

}
