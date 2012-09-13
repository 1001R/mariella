package org.mariella.persistence.annotations.processing;


import javax.persistence.TableGenerator;

public class TableGeneratorInfo {

TableGenerator tableGenerator;

public int getAllocationSize() {
	return tableGenerator.allocationSize();
}

void setTableGenerator(TableGenerator tableGenerator) {
	this.tableGenerator = tableGenerator;
}

public String getCatalog() {
	return tableGenerator.catalog();
}

public int getInitialValue() {
	return tableGenerator.initialValue();
}

public String getName() {
	return tableGenerator.name();
}

public String getPkColumnName() {
	return tableGenerator.pkColumnName();
}

public String getPkColumnValue() {
	return tableGenerator.pkColumnValue();
}

public String getSchema() {
	return tableGenerator.schema();
}

public String getTable() {
	return tableGenerator.table();
}

public UniqueConstraintInfo[] getUniqueConstraintInfos() {
	UniqueConstraintInfo[] infos = new UniqueConstraintInfo[tableGenerator.uniqueConstraints().length];
	for (int i=0; i<tableGenerator.uniqueConstraints().length;i++) {
		infos[i] = new UniqueConstraintInfo();
		infos[i].setUniqueConstraint(tableGenerator.uniqueConstraints()[i]);
	}
	return infos;
}

public String getValueColumnName() {
	return tableGenerator.valueColumnName();
}

}
