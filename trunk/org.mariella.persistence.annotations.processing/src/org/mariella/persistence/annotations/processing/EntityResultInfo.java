package org.mariella.persistence.annotations.processing;

import javax.persistence.EntityResult;

@SuppressWarnings("unchecked")
public class EntityResultInfo {

private EntityResult entityResult;

public Class getEntityClass() {
	return entityResult.entityClass();
}

public FieldResultInfo[] getFieldResultInfos() {
	FieldResultInfo[] result = new FieldResultInfo[entityResult.fields().length];
	for (int i=0; i<entityResult.fields().length;i++) {
		result[i] = new FieldResultInfo();
		result[i].setFieldResult(entityResult.fields()[i]);
	}
	return result;
}


public String getDiscriminatorColumn() {
	return entityResult.discriminatorColumn();
}

void setEntityResult(EntityResult entityResult) {
	this.entityResult = entityResult;
}

}
