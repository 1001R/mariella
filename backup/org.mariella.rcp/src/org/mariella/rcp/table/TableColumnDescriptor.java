package org.mariella.rcp.table;



public abstract class TableColumnDescriptor {

String header;
int weight;
TableColumnIconProvider iconProvider;

public TableColumnDescriptor(String header) {
	this.header = header;
	this.weight=20;
}

public TableColumnDescriptor(String header, int weight) {
	this.header = header;
	this.weight = weight;
}

public String getHeader() {
	return header;
}

public int getWeight() {
	return weight;
}

public TableColumnIconProvider getIconProvider() {
	return iconProvider;
}

public void setIconProvider(TableColumnIconProvider iconProvider) {
	this.iconProvider = iconProvider;
}



}
