package org.mariella.persistence.database;

import org.mariella.persistence.persistor.Row;

public interface BatchInsertStatementBuilder extends PreparedStatementBuilder {

public void addRow(Row row);

}
