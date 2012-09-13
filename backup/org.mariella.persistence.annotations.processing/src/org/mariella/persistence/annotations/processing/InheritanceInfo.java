package org.mariella.persistence.annotations.processing;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

public class InheritanceInfo {

private Inheritance inheritance;

public InheritanceType getStrategy() {
	return inheritance.strategy();
}

void setInheritance(Inheritance inheritance) {
	this.inheritance = inheritance;
}


}
