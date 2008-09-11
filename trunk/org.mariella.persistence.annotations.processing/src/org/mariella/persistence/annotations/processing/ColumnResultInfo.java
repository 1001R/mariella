package org.mariella.persistence.annotations.processing;

import javax.persistence.ColumnResult;

public class ColumnResultInfo {

private ColumnResult columnResult;

public String getName() {
	return columnResult.name();
}

void setColumnResult(ColumnResult columnResult) {
	this.columnResult = columnResult;
}

}
