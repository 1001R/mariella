package org.mariella.persistence.mapping;

import java.sql.SQLException;

import org.mariella.persistence.persistor.DatabaseAccess;

public abstract class ColumnValueGenerator {
public abstract boolean isGeneratedByDatabase();
public abstract Object generate(DatabaseAccess dba) throws SQLException;
}
