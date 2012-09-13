package org.mariella.persistence.annotations.processing;

import javax.persistence.JoinColumn;

import org.mariella.persistence.mapping.JoinColumnInfo;

public class JoinColumnInfoBuilder {
	
	JoinColumn joinColumn;
	
	JoinColumnInfoBuilder(JoinColumn joinColumn) {
		this.joinColumn = joinColumn;
	}

	public JoinColumnInfo buildJoinColumnInfo() {
		JoinColumnInfo info = new JoinColumnInfo();
		info.setColumnDefinition(joinColumn.columnDefinition());
		info.setInsertable(joinColumn.insertable());
		info.setName(joinColumn.name());
		info.setNullable(joinColumn.nullable());
		info.setReferencedColumnName(joinColumn.referencedColumnName());
		info.setUnique(joinColumn.unique());
		info.setUpdatable(joinColumn.updatable());
		return info;
	}
	
	

}
