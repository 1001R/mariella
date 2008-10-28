package org.mariella.persistence.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.persistor.Row;


public abstract class PreparedStatementBuilder {
	protected final Row row;
	protected List<Row> additialRows = new ArrayList<Row>();
	
public PreparedStatementBuilder(Row row) {
	super();
	this.row = row;
}
	
public Row getRow() {
	return row;
}

public abstract PreparedStatement createPreparedStatement(Connection connection);

public abstract String getSqlDebugString();

public boolean merge(PreparedStatementBuilder psb) {
	return false;
}

}
