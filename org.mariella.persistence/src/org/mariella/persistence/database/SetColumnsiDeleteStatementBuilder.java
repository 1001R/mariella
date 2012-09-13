package org.mariella.persistence.database;

import java.util.List;

import org.mariella.persistence.persistor.Row;

public class SetColumnsiDeleteStatementBuilder extends DeleteStatementBuilder {

public SetColumnsiDeleteStatementBuilder(Row row) {
	super(row);
}

@Override
public List<Column> getColumnsForWhereClause() {
	return row.getSetColumns();
}

}
