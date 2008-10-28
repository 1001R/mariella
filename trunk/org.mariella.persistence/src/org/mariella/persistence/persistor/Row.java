package org.mariella.persistence.persistor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.database.Column;
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
}
