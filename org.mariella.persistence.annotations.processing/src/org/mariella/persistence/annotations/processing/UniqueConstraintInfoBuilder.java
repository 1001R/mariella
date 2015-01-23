package org.mariella.persistence.annotations.processing;

import javax.persistence.UniqueConstraint;

import org.mariella.persistence.mapping.UniqueConstraintInfo;

public class UniqueConstraintInfoBuilder {
	
	private UniqueConstraint uniqueConstraint;
	private IModelToDb translator;
	
	public UniqueConstraintInfoBuilder(UniqueConstraint uniqueConstraint, IModelToDb translator) {
		this.uniqueConstraint = uniqueConstraint;
		this.translator = translator;
	}
	
	UniqueConstraintInfo buildUniqueConstraintInfo() {
		UniqueConstraintInfo info = new UniqueConstraintInfo();
		String[] names = uniqueConstraint.columnNames();
		for (int i = 0; i < names.length; i++) {
			names[i] = translator.translate(names[i]);
		}
		info.setColumnNames(names);
		return info;
	}

}
