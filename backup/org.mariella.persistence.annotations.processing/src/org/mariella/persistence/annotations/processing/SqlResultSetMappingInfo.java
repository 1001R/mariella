package org.mariella.persistence.annotations.processing;

import javax.persistence.SqlResultSetMapping;

public class SqlResultSetMappingInfo {
SqlResultSetMapping resultSetMapping;

public String getName() {
	return resultSetMapping.name();
}

public EntityResultInfo[] getEntityResultInfos() {
	EntityResultInfo[] result = new EntityResultInfo[resultSetMapping.entities().length];
	for (int i=0; i<resultSetMapping.entities().length;i++) {
		result[i] = new EntityResultInfo();
		result[i].setEntityResult(resultSetMapping.entities()[i]);
	}
	return result;
}

public ColumnResultInfo[] getColumnResultInfos() {
	ColumnResultInfo[] result = new ColumnResultInfo[resultSetMapping.columns().length];
	for (int i=0; i<resultSetMapping.columns().length;i++) {
		result[i] = new ColumnResultInfo();
		result[i].setColumnResult(resultSetMapping.columns()[i]);
	}
	return result;
}

void setResultSetMapping(SqlResultSetMapping resultSetMapping) {
	this.resultSetMapping = resultSetMapping;
}

}
