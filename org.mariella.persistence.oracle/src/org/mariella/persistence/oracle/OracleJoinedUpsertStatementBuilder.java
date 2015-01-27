package org.mariella.persistence.oracle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.mapping.JoinedClassMapping;
import org.mariella.persistence.mapping.PrimaryKeyJoinColumn;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.PersistenceStatementsManager;
import org.mariella.persistence.persistor.PersistenceStatementsManager.PersistenceStatement;
import org.mariella.persistence.persistor.Row;

public class OracleJoinedUpsertStatementBuilder extends SingleRowPreparedStatementBuilder {
	private interface BuildCallback {
		public void columnValue(StringBuilder b, Column column);
	}

	private final ObjectPersistor objectPersistor;
	private final JoinedClassMapping classMapping;

	public OracleJoinedUpsertStatementBuilder(ObjectPersistor objectPersistor, JoinedClassMapping classMapping, Row row) {
		super(row);
		this.objectPersistor = objectPersistor;
		this.classMapping = classMapping;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		for(PrimaryKeyJoinColumn primaryKeyJoinColumn : classMapping.getPrimaryKeyJoinColumns().getPrimaryKeyJoinColumns()) {
			Object value = objectPersistor.getModifiableAccessor().getValue(objectPersistor.getModificationInfo().getObject(), primaryKeyJoinColumn.getPrimaryKeyProperty());
			getRow().setProperty(primaryKeyJoinColumn.getJoinTableColumn(), value);
		}
	}

	
	@Override
	public void execute(PersistenceStatementsManager psManager) {
		final List<Column> columns = new ArrayList<Column>();
		String sql = buildSqlString(
			new BuildCallback() {
				@Override
				public void columnValue(StringBuilder b, Column column) {
					b.append('?');
					columns.add(column);
				}
			}
		);
		
		try {
			PersistenceStatement ps = psManager.prepareStatement(row.getTable().getName(), false, sql);
			int index = 1;
			for (Column column : columns) {
				column.setObject(ps.getPreparedStatement(), index++, row.getProperty(column));
			}
			ps.execute(getSqlDebugString());
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
