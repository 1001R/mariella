package org.mariella.oxygen.spring;

import java.sql.Connection;
import java.sql.SQLException;

import org.mariella.persistence.database.ConnectionCallback;
import org.mariella.persistence.persistor.DatabaseAccess;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateDatabaseAcceses extends DatabaseAccess {
	private final JdbcTemplate jdbcTemplate;

public JdbcTemplateDatabaseAcceses(JdbcTemplate jdbcTemplate) {
	super();
	this.jdbcTemplate = jdbcTemplate;
}

@Override
public Object doInConnection(final ConnectionCallback callback) throws SQLException {
	return jdbcTemplate.execute(
		new org.springframework.jdbc.core.ConnectionCallback<Object>() {
			@Override
			public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
				return callback.doInConnection(connection);
			}
		}
	);
}
}
