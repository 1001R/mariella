package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;


public abstract class AttributeInfo {

	private ClassInfo parentClassInfo;
	private AnnotatedElement annotatedElement;
	private String cachedName = null;
	
void resolveReferences() {}

public AttributeAccessType getAccessType() {
	return annotatedElement instanceof Field ? AttributeAccessType.FIELD : AttributeAccessType.PROPERTY; 
}

public ClassInfo getParentClassInfo() {
	return parentClassInfo;
}

void setParentClassInfo(ClassInfo parentClassInfo) {
	this.parentClassInfo = parentClassInfo;
}

public AnnotatedElement getAnnotatedElement() {
	return annotatedElement;
}

void setAnnotatedElement(AnnotatedElement annotatedElement) {
	this.annotatedElement = annotatedElement;
}

abstract AttributeInfo copyForAdoption();

void initializeAdoptionCopy(AttributeInfo copy) {
	copy.annotatedElement = annotatedElement;
	copy.cachedName = cachedName;
}

public void debugPrint(PrintStream out) {
	out.print("\t" + getName());
	out.print("\t-\t" + getClass().getSimpleName());
	debugPrintTypeInfo(out);
	debugPrintAttributes(out);
	out.println();
}

void debugPrintAttributes(PrintStream out) {
	if (isOptionalOrNullable())
		out.print(" @optional");
}

abstract void debugPrintTypeInfo(PrintStream out);


public String getName() {
	if (cachedName != null) return cachedName;
	cachedName = ReflectionUtil.readFieldOrPropertyName(annotatedElement);
	return cachedName;
}

// checks #optional, Column#nullable
public abstract boolean isOptionalOrNullable();

public String toString() {
	return parentClassInfo.getName() + "." + getName();
}

public void overrideWith(AttributeInfo overriddenAttrInfo) {
	
}

}


