package org.mariella.persistence.annotations.processing;

import javax.persistence.UniqueConstraint;

import org.mariella.persistence.mapping.UniqueConstraintInfo;

public class UniqueConstraintInfoBuilder {
	
	UniqueConstraint uniqueConstraint;
	
	public UniqueConstraintInfoBuilder(UniqueConstraint uniqueConstraint) {
		this.uniqueConstraint = uniqueConstraint;
	}
	
	UniqueConstraintInfo buildUniqueConstraintInfo() {
		UniqueConstraintInfo info = new UniqueConstraintInfo();
		info.setColumnNames(uniqueConstraint.columnNames());
		return info;
	}

}
