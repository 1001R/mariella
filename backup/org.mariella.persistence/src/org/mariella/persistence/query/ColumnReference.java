package org.mariella.persistence.query;


import org.mariella.persistence.database.Column;

public class ColumnReference implements Expression {
	private Column column;
	private Expression tableReference;
	
public ColumnReference(Expression tableReference, Column column) {
	super();
	this.tableReference = tableReference;
	this.column = column;
}
	
public Column getColumn() {
	return column;
}

public void setColumn(Column column) {
	this.column = column;
}

public Expression getTableReference() {
	return tableReference;
}

public void setTableReference(Expression tableReference) {
	this.tableReference = tableReference;
}

public void printSql(StringBuilder b) {
	tableReference.printSql(b);
	b.append('.');
	b.append(column.getName());
}

}
