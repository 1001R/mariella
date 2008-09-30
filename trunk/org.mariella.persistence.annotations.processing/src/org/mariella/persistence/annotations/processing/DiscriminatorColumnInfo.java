package org.mariella.persistence.annotations.processing;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;

public class DiscriminatorColumnInfo {
	DiscriminatorColumn discriminatorColumn;

public DiscriminatorColumn getDiscriminatorColumn() {
	return discriminatorColumn;
}

void setDiscriminatorColumn(DiscriminatorColumn discriminatorColumn) {
	this.discriminatorColumn = discriminatorColumn;
}
	
public String getName() {
	return discriminatorColumn.name();
}

public DiscriminatorType getDiscriminatorType() {
	return discriminatorColumn.discriminatorType();
}

public String getColumnDefinition() {
	return discriminatorColumn.columnDefinition();
}

public int getLength() {
	return discriminatorColumn.length();
}

}
