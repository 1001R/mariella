package org.mariella.persistence.annotations.processing;

import javax.persistence.FieldResult;

public class FieldResultInfo {

private FieldResult fieldResult;


public String getName() {
	return fieldResult.name();
}

public String getColumn() {
	return fieldResult.column();
}

void setFieldResult(FieldResult fieldResult) {
	this.fieldResult = fieldResult;
}

}
