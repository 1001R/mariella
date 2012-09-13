package org.mariella.persistence.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
	private String name;
	private String schema;
	private String catalog;
	private Map<String, Column> columns = new HashMap<String, Column>();
	private List<Column> primaryKey = new ArrayList<Column>();
	
public Table(String catalog, String schema, String name) {
	super();
	this.name = name;
	this.schema = schema;
	this.catalog = catalog;
}
	
public String getName() {
	return name;
}

public String getSchema() {
	return schema;
}

public String getCatalog() {
	return catalog;
}

public Collection<Column> getColumns() {
	return columns.values();
}

public void addColumn(Column column) {
	columns.put(column.getName(), column);
}

public void addPrimaryKeyColumn(Column column) {
	addColumn(column);
	primaryKey.add(column);
}

public Column getColumn(String name) {
	return columns.get(name);
}

public List<Column> getPrimaryKey() {
	return primaryKey;
}

@Override
public String toString() {
	return name;
}
}
