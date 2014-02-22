package org.mariella.persistence.oracle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Column;
import org.mariella.persistence.persistor.PreparedStatementManager;
import org.mariella.persistence.persistor.Row;


public class OracleBatchInsertStatementBuilder implements BatchInsertStatementBuilder{
	private List<Row> rows = new ArrayList<Row>();
	
public OracleBatchInsertStatementBuilder() {
	super();
}

@Override
public void initialize() {
}

public OracleBatchInsertStatementBuilder(Row row) {
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
public void execute(PreparedStatementManager psManager) {
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
			PreparedStatement ps = psManager.prepareStatement(getTemplateRow().getTable().getName(), b.toString());
			try {
				for(Row row : rows) {
					int index = 1;
					for(Column column : row.getSetColumns()) {
						column.setObject(ps, index, row.getProperty(column));
						index++;
					}
					psManager.prepared(ps);
				}
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
