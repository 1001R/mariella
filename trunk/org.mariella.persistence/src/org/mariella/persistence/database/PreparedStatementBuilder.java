package org.mariella.persistence.database;

import org.mariella.persistence.persistor.PersistenceStatementsManager;

public interface PreparedStatementBuilder {

public void initialize();
public void execute(PersistenceStatementsManager psManager);
public String getSqlDebugString();

}
