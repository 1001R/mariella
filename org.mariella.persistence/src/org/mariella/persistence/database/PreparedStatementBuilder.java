package org.mariella.persistence.database;

import java.sql.Connection;

public interface PreparedStatementBuilder {

public void initialize();
public void execute(Connection connection);
public String getSqlDebugString();

}
