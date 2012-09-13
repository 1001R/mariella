package org.mariella.persistence.schema;

import java.beans.PropertyDescriptor;

public abstract class PropertyDescription {
	private final ClassDescription classDescription;
	private final PropertyDescriptor propertyDescriptor;

public PropertyDescription(ClassDescription classDescription, PropertyDescriptor propertyDescriptor) {
	this.classDescription = classDescription;
	this.propertyDescriptor = propertyDescriptor;
}
	
public PropertyDescriptor getPropertyDescriptor() {
	return propertyDescriptor;
}

public ClassDescription getClassDescription() {
	return classDescription;
}

public String toString() {
	return classDescription.toString() + "." + getPropertyDescriptor().getName();
}
}
