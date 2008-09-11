package org.mariella.persistence.annotations.processing;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

public class GeneratedValueInfo {
GeneratedValue generatedValue;

void setGeneratedValue(GeneratedValue generatedValue) {
	this.generatedValue = generatedValue;
}

public String getGenerator() {
	return generatedValue.generator();
}

public GenerationType getStrategy() {
	return generatedValue.strategy();
}

}
