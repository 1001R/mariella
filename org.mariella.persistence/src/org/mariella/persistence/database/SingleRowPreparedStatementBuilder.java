package org.mariella.persistence.database;

import org.mariella.persistence.persistor.Row;

public abstract class SingleRowPreparedStatementBuilder extends PreparedStatementBuilder {
	protected final Row row;

public SingleRowPreparedStatementBuilder(Row row) {
	super();
	this.row = row;
}

public Row getRow() {
	return row;
}

}
