package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.persistor.PreparedStatementManager;
import org.mariella.persistence.persistor.Row;

public class UpsertStatementBuilder extends SingleRowPreparedStatementBuilder {
	private interface BuildCallback {
		public void columnValue(StringBuilder b, Column column);
	}
	
	
public UpsertStatementBuilder(Row row) {
	super(row);
}

@Override
public void execute(PreparedStatementManager psManager) {
	String sql = buildSqlString(
		new BuildCallback() {
			@Override
			public void columnValue(StringBuilder b, Column column) {
				b.append('?');
			}
		}
	);
	
	try {
		PreparedStatement ps = psManager.prepareStatement(row.getTable().getName(), sql);
		int index = 1;
		for(Column pk : row.getTable().getPrimaryKey()) {
			pk.setObject(ps, index, row.getProperty(pk));
			index++;
		}
		for(Column column : row.getSetColumns()) {
			column.setObject(ps, index, row.getProperty(column));
			index++;
		}
		for(Column column : row.getSetColumns()) {
			if(!row.getTable().getPrimaryKey().contains(column)) {
				column.setObject(ps, index, row.getProperty(column));
				index++;
			}
		}
		psManager.prepared(ps);
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}	
}

@Override
public String getSqlDebugString() {
	return buildSqlString(
		new BuildCallback() {
			@Override
			public void columnValue(StringBuilder b, Column column) {
				b.append(row.getProperty(column));
			}
		}
	);
}

private String buildSqlString(BuildCallback buildCallback) {
	boolean first;
	StringBuilder b = new StringBuilder();
	b.append("MERGE INTO " + row.getTable().getName());
	b.append("\n\tUSING DUAL ON (");
	
	first = true;
	for(Column pk : row.getTable().getPrimaryKey()) {
		if(first) first = false;
		else b.append(" AND ");
		b.append(pk.getName());
		b.append(" = ");
		buildCallback.columnValue(b, pk);
	}
	b.append(")");
	b.append("\nWHEN NOT MATCHED THEN");
	b.append("\n\tINSERT (");
	
	first = true;
	for(Column column : row.getSetColumns()) {
		if(first) {
			first = false;
		} else {
			b.append(", ");
		}
		b.append(column.getName());
	}
	b.append(") VALUES (");
	first = true;
	for(Column column : row.getSetColumns()) {
		if(first) {
			first = false;
		} else {
			b.append(", ");
		}
		buildCallback.columnValue(b, column);
	}
	b.append(")");
	List<Column> columnsToUpdate = new ArrayList<Column>();
	for(Column column : row.getSetColumns()) {
		if(!row.getTable().getPrimaryKey().contains(column)) {
			columnsToUpdate.add(column);
		}
	}
	if (!columnsToUpdate.isEmpty()) {
		b.append("\nWHEN MATCHED THEN");
		b.append("\n\tUPDATE SET ");
		first = true;
		for(Column column : columnsToUpdate) {
			if(first) {
				first = false;
			} else {
				b.append(", ");
			}
			b.append(column.getName());
			b.append(" = ");
			buildCallback.columnValue(b, column);
		}
	}
	return b.toString();
}

}
