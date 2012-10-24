package org.mariella.oxygen.runtime.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.PersistenceException;

import org.mariella.oxygen.runtime.core.OxyConnectionProvider;


/**
 * @author aim
 */
public class OxyJ2SEConnectionProvider implements OxyConnectionProvider {
	public static final String CONNECT_PROPERTY_NAME = "org.mariella.oxygen.connectstring"; 
	public static final String DBUSER_PROPERTY_NAME = "org.mariella.oxygen.dbuser";
	public static final String DBPASSWORD_PROPERTY_NAME = "org.mariella.oxygen.dbpassword";

	private Connection connection = null;
	private String connectString = null;
	private String dbUser = null;
	private String dbPassword = null;
	
public OxyJ2SEConnectionProvider(String connectString) {
	super();
	this.connectString = connectString;
}

public OxyJ2SEConnectionProvider(String connectString, String dbUser, String dbPassword) {
	super();
	this.connectString = connectString;
	this.dbUser = dbUser;
	this.dbPassword = dbPassword;
}

public void close() {
	if(connection != null) {
		try {
			connection.close();
		} catch(SQLException e) {
			throw new PersistenceException(e);
		}
	}
}

public Connection getConnection() {
	if(connection == null) {
		if(connectString == null) {
			throw new IllegalStateException("No connect string specified. Please specify with the " + CONNECT_PROPERTY_NAME + " property in your persistence.xml");
		}
		try {
			if(dbUser == null) {
				connection = DriverManager.getConnection(connectString);
			} else {
				connection = DriverManager.getConnection(connectString, dbUser, dbPassword);
			}
			connection.setAutoCommit(false);
		} catch(SQLException e) {
			throw new PersistenceException(e);
		}
	}
	return connection;
}

}
