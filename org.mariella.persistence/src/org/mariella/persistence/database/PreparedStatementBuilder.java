package org.mariella.persistence.database;

import java.sql.Connection;

public abstract class PreparedStatementBuilder {

public PreparedStatementBuilder() {
	super();
}

public void initialize() {
}

public abstract void execute(Connection connection);

public abstract String getSqlDebugString();

}
