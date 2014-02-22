package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.mariella.persistence.persistor.PreparedStatementManager;
import org.mariella.persistence.persistor.Row;


public abstract class DeleteStatementBuilder extends SingleRowPreparedStatementBuilder {

public DeleteStatementBuilder(Row row) {
	super(row);
}

public abstract List<Column> getColumnsForWhereClause();

@Override
public void execute(PreparedStatementManager psManager) {
	StringBuilder b = new StringBuilder();
	b.append("DELETE FROM ");
	b.append(row.getTable().getName());
	b.append(" WHERE ");
	
	boolean first = true;
	for(Column column : getColumnsForWhereClause()) {
		if(first) first = false;
		else b.append(" AND ");
		b.append(column.getName());
		b.append(" = ?");
	}
	
	try {
		PreparedStatement ps = psManager.prepareStatement(row.getTable().getName(), b.toString());
		int index = 1;
		for(Column column : getColumnsForWhereClause()) {
			column.setObject(ps, index, row.getProperty(column));
			index++;
		}
		psManager.prepared(ps);
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}	
}

@Override
public String getSqlDebugString() {
	StringBuilder b = new StringBuilder();
	b.append("DELETE FROM ");
	b.append(row.getTable().getName());
	b.append(" WHERE ");
	boolean first = true;
	for(Column column : getColumnsForWhereClause()) {
		if(first) first = false;
		else b.append(" AND ");
		b.append(column.getName());
		b.append(" = ");
		b.append(row.getProperty(column));
	}
	return b.toString();	
}

}
