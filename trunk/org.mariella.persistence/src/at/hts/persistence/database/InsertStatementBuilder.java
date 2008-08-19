package at.hts.persistence.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import at.hts.persistence.persistor.Row;

public class InsertStatementBuilder extends PreparedStatementBuilder {

public InsertStatementBuilder(Row row) {
	super(row);
}

@Override
public PreparedStatement createPreparedStatement(Connection connection) {
	StringBuilder b = new StringBuilder();
	b.append("INSERT INTO ");
	b.append(row.getTable().getName());
	b.append(" (");
	boolean first = true;
	for(Column column : row.getSetColumns()) {
		if(first) first = false;
		else b.append(", ");
		b.append(column.getName());
	}
	b.append(") VALUES (");
	for(int i=0; i < row.getSetColumns().size(); i++) {
		if(i > 0) b.append(", ");
		b.append("?");
	}
	b.append(")");
	try {
		PreparedStatement ps = connection.prepareStatement(b.toString());
		int index = 1;
		for(Column column : row.getSetColumns()) {
			column.setObject(ps, index, row.getProperty(column));
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
	b.append("INSERT INTO ");
	b.append(row.getTable().getName());
	b.append(" (");
	boolean first = true;
	for(Column column : row.getSetColumns()) {
		if(first) first = false;
		else b.append(", ");
		b.append(column.getName());
	}
	b.append(") VALUES (");
	first = true;
	for(Column column : row.getSetColumns()) {
		if(first) first = false;
		else b.append(", ");
		b.append(row.getProperty(column));
	}
	b.append(")");
	return b.toString();
}

}
