package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mariella.persistence.persistor.PersistenceStatementsManager;
import org.mariella.persistence.persistor.PersistenceStatementsManager.PersistenceStatement;
import org.mariella.persistence.persistor.Row;


public class InsertStatementBuilder extends SingleRowPreparedStatementBuilder {

public InsertStatementBuilder(Row row) {
	super(row);
}

@Override
public void execute(PersistenceStatementsManager psManager) {
	try {
		String sql = getInsertString();
		PersistenceStatement ps = psManager.prepareStatement(row.getTable().getName(), false, sql);
		setParameters(ps.getPreparedStatement());
		ps.execute(getSqlDebugString());
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}
}

protected String getInsertString() {
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
	return b.toString();
}

protected void setParameters(PreparedStatement ps) throws SQLException {
	int index = 1;
	for(Column column : row.getSetColumns()) {
		column.setObject(ps, index, row.getProperty(column));
		index++;
	}
}

@SuppressWarnings("unchecked")
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
		@SuppressWarnings("rawtypes")
		Converter conv = column.getConverter();
		b.append(conv.toString(row.getProperty(column)));
	}
	b.append(")");
	return b.toString();
}

}
