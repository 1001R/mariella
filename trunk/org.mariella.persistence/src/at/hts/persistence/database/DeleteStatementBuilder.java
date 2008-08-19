package at.hts.persistence.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import at.hts.persistence.persistor.Row;

public class DeleteStatementBuilder extends PreparedStatementBuilder {

public DeleteStatementBuilder(Row row) {
	super(row);
}

@Override
public PreparedStatement createPreparedStatement(Connection connection) {
	StringBuilder b = new StringBuilder();
	b.append("DELETE FROM ");
	b.append(row.getTable().getName());
	b.append(" WHERE ");
	
	boolean first = true;
	for(Column pk : row.getTable().getPrimaryKey()) {
		if(first) first = false;
		else b.append(" AND ");
		b.append(pk.getName());
		b.append(" = ?");
	}
	
	try {
		PreparedStatement ps = connection.prepareStatement(b.toString());
		int index = 1;
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
	b.append("DELETE FROM ");
	b.append(row.getTable().getName());
	b.append(" WHERE ");
	boolean first = true;
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
