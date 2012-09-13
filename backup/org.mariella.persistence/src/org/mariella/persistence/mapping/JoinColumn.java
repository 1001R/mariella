package org.mariella.persistence.mapping;

import org.mariella.persistence.database.Column;

public class JoinColumn {
	private Column myColumn;
	private Column referencedColumn;
	private boolean insertable;
	private boolean updatable;

public Column getMyColumn() {
	return myColumn;
}

public void setMyColumn(Column myColumn) {
	this.myColumn = myColumn;
}

public Column getReferencedColumn() {
	return referencedColumn;
}

public void setReferencedColumn(Column referencedColumn) {
	this.referencedColumn = referencedColumn;
}

public boolean isInsertable() {
	return insertable;
}

public void setInsertable(boolean insertable) {
	this.insertable = insertable;
}

public boolean isUpdatable() {
	return updatable;
}

public void setUpdatable(boolean updatable) {
	this.updatable = updatable;
}

}
