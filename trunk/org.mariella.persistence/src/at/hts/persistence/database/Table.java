package at.hts.persistence.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
	private String name;
	private Map<String, Column> columns = new HashMap<String, Column>();
	private List<Column> primaryKey = new ArrayList<Column>();
	
public Table(String name) {
	super();
	this.name = name;
}
	
public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getSchema() {
	return null;
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
