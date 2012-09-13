package org.mariella.persistence.annotations.processing;

import org.mariella.persistence.annotations.DomainDefinition;

public class DomainDefinitionInfo {
private DomainDefinition domainDefinition;

DomainDefinition getDomainDefinition() {
	return domainDefinition;
}

void setDomainDefinition(DomainDefinition domainDefinition) {
	this.domainDefinition = domainDefinition;
}

public int getLength() {
	return domainDefinition.length();
}

public String getName() {
	return domainDefinition.name();
}

public int getPrecision() {
	return domainDefinition.precision();
}

public int getScale() {
	return domainDefinition.scale();
}

public String getSqlType() {
	return domainDefinition.sqlType();
}

}
