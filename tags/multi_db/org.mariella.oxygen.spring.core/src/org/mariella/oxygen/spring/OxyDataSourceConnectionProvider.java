package org.mariella.oxygen.spring;


import java.sql.Connection;

import javax.sql.DataSource;

import org.mariella.oxygen.runtime.core.OxyConnectionProvider;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author mart
 */
public class OxyDataSourceConnectionProvider implements OxyConnectionProvider {

	private DataSource dataSource;

public OxyDataSourceConnectionProvider(DataSource ds) {
	this.dataSource = ds;
}

public void close() {
	throw new UnsupportedOperationException();
}

public Connection getConnection() {
	return ((ConnectionHolder)TransactionSynchronizationManager.getResource(dataSource)).getConnection();
}

}
