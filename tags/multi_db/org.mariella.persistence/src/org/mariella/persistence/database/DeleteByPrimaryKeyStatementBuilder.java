package org.mariella.persistence.database;

import java.util.List;

import org.mariella.persistence.persistor.Row;

public class DeleteByPrimaryKeyStatementBuilder extends DeleteStatementBuilder {

public DeleteByPrimaryKeyStatementBuilder(Row row) {
	super(row);
}

@Override
public List<Column> getColumnsForWhereClause() {
	return row.getTable().getPrimaryKey(); 
}

}
