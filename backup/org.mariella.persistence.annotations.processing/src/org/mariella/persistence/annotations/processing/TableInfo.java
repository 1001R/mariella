package org.mariella.persistence.annotations.processing;

import javax.persistence.Table;


public class TableInfo {

private Table table;

public String getCatalog() {
	return table.catalog();
}

public String getName() {
	return table.name();
}

public String getSchema() {
	return table.schema();
}

void setTable(Table table) {
	this.table = table;
}

@Override
public boolean equals(Object obj) {
	return getSchema().equals(((TableInfo)obj).getSchema()) && getName().equals(((TableInfo)obj).getName());
}

@Override
public int hashCode() {
	return getName().hashCode();
}

}
