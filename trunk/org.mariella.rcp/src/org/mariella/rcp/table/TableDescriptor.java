package org.mariella.rcp.table;



public class TableDescriptor {

private Class objectClass;
private TableColumnDescriptor[] columnDescriptors;



public TableDescriptor(Class objectClass, TableColumnDescriptor[] columnDescriptors) {
	this.objectClass = objectClass;
	this.columnDescriptors = columnDescriptors;
}

public TableColumnDescriptor[] getColumnDescriptors() {
	return columnDescriptors;
}

public void setColumnDescriptors(TableColumnDescriptor[] columnDescriptors) {
	this.columnDescriptors = columnDescriptors;
}

public Class getObjectClass() {
	return objectClass;
}

public void setObjectClass(Class objectClass) {
	this.objectClass = objectClass;
}

public TablePropertyColumnDescriptor getColumnDescriptor(String property) {
	for (TableColumnDescriptor col : columnDescriptors)
		if (col instanceof TablePropertyColumnDescriptor && ((TablePropertyColumnDescriptor)col).propertyPath.equals(property))
			return (TablePropertyColumnDescriptor)col;
	return null;
}

}
