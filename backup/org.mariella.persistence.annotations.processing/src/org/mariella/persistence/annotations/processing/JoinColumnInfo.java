package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;

import javax.persistence.JoinColumn;

public class JoinColumnInfo {

	private JoinColumn joinColumn;

public JoinColumnInfo() {
}	
	
public String getName() {
	return joinColumn.name();
}

public String getReferencedColumnName() {
	return joinColumn.referencedColumnName();
}

public boolean isUnique() {
	return joinColumn.unique();
}

public boolean isNullable() {
	return joinColumn.nullable();
}

public boolean isInsertable() {
	return joinColumn.insertable();
}

public boolean isUpdatable() {
	return joinColumn.updatable();
}

public String getColumnDefinition() {
	return joinColumn.columnDefinition();
}

public String getTable() {
	return joinColumn.table();
}

void setJoinColumn(JoinColumn joinColumn) {
	this.joinColumn = joinColumn;
}

public void debugPrint(PrintStream out) {
	out.print(" @JoinColumn " + getName());
}

}
