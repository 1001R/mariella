package org.mariella.persistence.database;

import org.mariella.persistence.persistor.Row;

public abstract class SingleRowPreparedStatementBuilder implements PreparedStatementBuilder {
	protected final Row row;

public SingleRowPreparedStatementBuilder(Row row) {
	super();
	this.row = row;
}

@Override
public void initialize() {
}

public Row getRow() {
	return row;
}

}
