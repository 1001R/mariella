package at.hts.persistence.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import at.hts.persistence.persistor.Row;

public class UpdateStatementBuilder extends PreparedStatementBuilder {

public UpdateStatementBuilder(Row row) {
	super(row);
}

@Override
public PreparedStatement createPreparedStatement(Connection connection) {
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
		PreparedStatement ps = connection.prepareStatement(b.toString());
		int index = 1;
		for(Column column : row.getSetColumns()) {
			if(!row.getTable().getPrimaryKey().contains(column)) {
				column.setObject(ps, index, row.getProperty(column));
				index++;
			}
		}
		for(Column pk : row.getTable().getPrimaryKey()) {
			pk.setObject(ps, index, row.getProperty(pk));
			index++;
		}
		return ps;
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
