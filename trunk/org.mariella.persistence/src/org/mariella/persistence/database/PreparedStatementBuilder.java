package org.mariella.persistence.database;

import org.mariella.persistence.persistor.PreparedStatementManager;

public interface PreparedStatementBuilder {

public void initialize();
public void execute(PreparedStatementManager psManager);
public String getSqlDebugString();

}
