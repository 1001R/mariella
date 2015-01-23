package org.mariella.persistence.postgres;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Column;
import org.mariella.persistence.persistor.PersistenceStatementsManager;
import org.mariella.persistence.persistor.Row;
import org.mariella.persistence.persistor.PersistenceStatementsManager.PersistenceStatement;


public class PostgresBatchInsertStatementBuilder implements BatchInsertStatementBuilder{
	private List<Row> rows = new ArrayList<Row>();
	
public PostgresBatchInsertStatementBuilder() {
	super();
}

@Override
public void initialize() {
}

public PostgresBatchInsertStatementBuilder(Row row) {
	super();
	rows.add(row);
}

@Override
public void addRow(Row row) {
	rows.add(row);
}

private Row getTemplateRow() {
	return rows.get(0);
}

private boolean isBatch() {
	return rows.size() > 1;
}

@Override
public void execute(PersistenceStatementsManager psManager) {
	if(!rows.isEmpty()) {
		StringBuilder b = new StringBuilder();
		b.append("INSERT INTO ");
		b.append(getTemplateRow().getTable().getName());
		b.append(" (");
		boolean first = true;
		for(Column column : getTemplateRow().getSetColumns()) {
			if(first) first = false;
			else b.append(", ");
			b.append(column.getName());
		}
		b.append(") VALUES (");
		for(int i=0; i < getTemplateRow().getSetColumns().size(); i++) {
			if(i > 0) b.append(", ");
			b.append("?");
		}
		b.append(")");
		try {
			PersistenceStatement ps = psManager.prepareBatchedStatement(getTemplateRow().getTable().getName(), false, b.toString());
			try {
				for(Row row : rows) {
					int index = 1;
					for(Column column : row.getSetColumns()) {
						column.setObject(ps.getPreparedStatement(), index, row.getProperty(column));
						index++;
					}
					ps.execute(null);
				}
				ps.executeBatch();
			} finally {
				ps.close();
			}
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}

@Override
public String getSqlDebugString() {
	StringBuilder b = new StringBuilder();
	if(rows.isEmpty()) {
		b.append("EMTPY INSERT STATEMENT!!!!");
	} else {
		b.append("INSERT INTO ");
		b.append(getTemplateRow().getTable().getName());
		b.append(" (");
		boolean first = true;
		for(Column column : getTemplateRow().getSetColumns()) {
			if(first) first = false;
			else b.append(", ");
			b.append(column.getName());
		}
		b.append(") VALUES (");
		if(isBatch()) {
			b.append("batch");
		} else {
			Row row = rows.get(0);
			first = true;
			for(Column column : row.getSetColumns()) {
				if(first) first = false;
				else b.append(", ");
				b.append(row.getProperty(column));
			}
		}
		b.append(")");
	}
	return b.toString();
}

}
