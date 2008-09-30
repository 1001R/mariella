package org.mariella.persistence.annotations.processing;

import javax.persistence.DiscriminatorValue;

public class DiscriminatorValueInfo {
	DiscriminatorValue discriminatorValue;

public DiscriminatorValue getDiscriminatorValuen() {
	return discriminatorValue;
}

void setDiscriminatorValue(DiscriminatorValue discriminatorValue) {
	this.discriminatorValue = discriminatorValue;
}
	
public String getValue() {
	return discriminatorValue.value();
}

}
