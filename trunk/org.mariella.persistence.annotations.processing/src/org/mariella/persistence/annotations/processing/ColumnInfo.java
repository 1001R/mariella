package org.mariella.persistence.annotations.processing;



import java.io.PrintStream;

import javax.persistence.Column;

public class ColumnInfo {
private Column column;

public String getName() {
	return column.name();
}

public boolean isUnique() {
	return column.unique();
}

public boolean isNullable() {
	return column.nullable();
}

public boolean isInsertable() {
	return column.insertable();
}

public boolean isUpdatable() {
	return column.updatable();
}

public String getColumnDefinition() {
	return column.columnDefinition();
}

public String getTable() {
	return column.table();
}

public int getLength() {
	return column.length();
}

public int getPrecision() {
	return column.precision();
}

public int getScale() {
	return column.scale();
}

void setColumn(Column column) {
	this.column = column;
}

public void debugPrint(PrintStream out) {
	out.print(" [column");
	out.print(" name: " + getName());
	out.print("]");
	
}



}
