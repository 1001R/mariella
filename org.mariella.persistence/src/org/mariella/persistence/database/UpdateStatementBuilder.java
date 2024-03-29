package org.mariella.persistence.database;

import java.sql.SQLException;

import org.mariella.persistence.persistor.PersistenceStatementsManager;
import org.mariella.persistence.persistor.PersistenceStatementsManager.PersistenceStatement;
import org.mariella.persistence.persistor.Row;


public class UpdateStatementBuilder extends SingleRowPreparedStatementBuilder {

public UpdateStatementBuilder(Row row) {
	super(row);
}

@Override
public void execute(PersistenceStatementsManager psManager) {
	StringBuilder b = new StringBuilder();
	b.append("UPDATE ");
	b.append(row.getTable().getName());
	b.append(" SET ");
	boolean first = true;
	for(Column column : row.getSetColumns()) {
		if(!row.getTable().getPrimaryKey().contains(column)) {
			if(first) first = false;
			else b.append(", ");
			b.append(column.getName());
			b.append(" = ?");
		} 
	}
	b.append(" WHERE ");
	
	first = true;
	for(Column pk : row.getTable().getPrimaryKey()) {
		if(first) first = false;
		else b.append(" AND ");
		b.append(pk.getName());
		b.append(" = ?");
	}
	
	try {
		PersistenceStatement ps = psManager.prepareStatement(row.getTable().getName(), false, b.toString());
		int index = 1;
		for(Column column : row.getSetColumns()) {
			if(!row.getTable().getPrimaryKey().contains(column)) {
				column.setObject(ps.getPreparedStatement(), index, row.getProperty(column));
				index++;
			}
		}
		for(Column pk : row.getTable().getPrimaryKey()) {
			pk.setObject(ps.getPreparedStatement(), index, row.getProperty(pk));
			index++;
		}
		ps.execute(getSqlDebugString());
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}	
}

@Override
public String getSqlDebugString() {
	StringBuilder b = new StringBuilder();
	b.append("UPDATE ");
	b.append(row.getTable().getName());
	b.append(" SET ");
	boolean first = true;
	for(Column column : row.getSetColumns()) {
		if(!row.getTable().getPrimaryKey().contains(column)) {
			if(first) first = false;
			else b.append(", ");
			b.append(column.getName());
			b.append(" = ");
			b.append(row.getProperty(column));
		} 
	}
	b.append(" WHERE ");
	first = true;
	for(Column pk : row.getTable().getPrimaryKey()) {
		if(first) first = false;
		else b.append(" AND ");
		b.append(pk.getName());
		b.append(" = ");
		b.append(row.getProperty(pk));
	}
	return b.toString();
}
}
