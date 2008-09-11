package org.mariella.persistence.annotations.processing;

import javax.persistence.UniqueConstraint;

public class UniqueConstraintInfo {

UniqueConstraint uniqueConstraint;

public String[] getColumnNames() {
	return uniqueConstraint.columnNames();
}

void setUniqueConstraint(UniqueConstraint uniqueConstraint) {
	this.uniqueConstraint = uniqueConstraint;
}

}
