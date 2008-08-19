package org.mariella.glue.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.mariella.glue.core.Activator;


public class TransactionalRunner {
	private DataSource dataSource;
	private Connection connection;
	
public TransactionalRunner(DataSource dataSource) {
	super();
	this.dataSource = dataSource;
}

public Connection getConnection() {
	if(connection == null) {
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	return connection;
}

public <T> T run(TransactionalRunnable<T> runnable) {
	try {
		T result = runnable.run(this);
		if(connection != null) {
			Activator.logger.info("COMMIT");
			connection.commit();
		}
		return result;
	} catch(Exception e) {
		if(connection != null) {
			try {
				Activator.logger.info("ROLLBACK");
				connection.rollback();
			} catch(SQLException e1) {
				throw new RuntimeException(e1);
			}
		}
		if(e instanceof RuntimeException) {
			throw (RuntimeException)e;
		} else {
			throw new RuntimeException(e);
		}
	} finally {
		try {
			Activator.logger.info("Closing Connection");
			connection.close();
		} catch(SQLException e1) {
			throw new RuntimeException(e1);
		}
	}
}

}
