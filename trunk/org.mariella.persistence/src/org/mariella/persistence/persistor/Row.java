package org.mariella.persistence.persistor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.Converter;
import org.mariella.persistence.database.Table;


public class Row {
	private final Table table;
	private Map<Column, Object> valueMap = new HashMap<Column, Object>();
	private List<Column> setColumns = new ArrayList<Column>();
	
public Row(Table table) {
	super();
	this.table = table;
}

public Table getTable() {
	return table;
}
	
public List<Column> getSetColumns() {
	return setColumns;
}
	
public Object getProperty(Column column) {
	if(!setColumns.contains(column)) {
		throw new IllegalArgumentException("No value available for column");
	}
	return valueMap.get(column);
}

public void setProperty(Column column, Object value) {
	valueMap.put(column, value);
	if(!setColumns.contains(column)) {
		setColumns.add(column);
	}
}

@Override
@SuppressWarnings("unchecked")
public String toString() {
	StringBuilder b = new StringBuilder();
	b.append(table.getName());
	b.append("(");
	boolean first = true;
	for(Column column : setColumns) {
		if(first) {
			first = false;
		} else {
			b.append(", ");
		}
		b.append(column.getName() + ":" + ((Converter<Object>)column.getConverter()).toString(valueMap.get(column)));
	}
	b.append(")");
	return b.toString();
}

}
