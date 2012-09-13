package org.mariella.persistence.annotations.mapping_builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTableInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String catalog;
	private String schema;
	private Map<String, DatabaseColumnInfo> columnInfos = new HashMap<String, DatabaseColumnInfo>();
	private List<DatabaseColumnInfo> primaryKey = new ArrayList<DatabaseColumnInfo>();
	
public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getCatalog() {
	return catalog;
}

public void setCatalog(String catalog) {
	this.catalog = catalog;
}

public String getSchema() {
	return schema;
}

public void setSchema(String schema) {
	this.schema = schema;
}

public void addColumnInfo(DatabaseColumnInfo columnInfo) {
	columnInfos.put(columnInfo.getName(), columnInfo);
}

public Collection<DatabaseColumnInfo> getColumnInfos() {
	return columnInfos.values();
}

public DatabaseColumnInfo getColumnInfo(String columnName) {
	return columnInfos.get(columnName);
}

public List<DatabaseColumnInfo> getPrimaryKey() {
	return primaryKey;
}

}
