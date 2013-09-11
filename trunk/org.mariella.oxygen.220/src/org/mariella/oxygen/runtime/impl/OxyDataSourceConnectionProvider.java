package org.mariella.oxygen.runtime.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import org.mariella.oxygen.runtime.core.OxyConnectionProvider;

public class OxyDataSourceConnectionProvider implements OxyConnectionProvider {
	private final DataSource dataSource;
	private Connection connection;
	
public OxyDataSourceConnectionProvider(DataSource dataSource) {
	super();
	this.dataSource = dataSource;
}
	
public Connection getConnection() {
	if(connection == null) {
		try {
			connection = dataSource.getConnection();
		} catch(SQLException e) {
			throw new PersistenceException(e);
		}
	}
	return connection;

}

public void close() {
	if(connection != null) {
		try {
			connection = dataSource.getConnection();
		} catch(SQLException e) {
			throw new PersistenceException(e);
		} finally {
			connection = null;
		}
	}
}

}
