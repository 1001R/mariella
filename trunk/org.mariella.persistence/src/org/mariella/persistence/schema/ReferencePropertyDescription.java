package org.mariella.persistence.schema;

import java.beans.PropertyDescriptor;

public class ReferencePropertyDescription extends RelationshipPropertyDescription {
	
	protected final boolean updateForeignKeys;

public ReferencePropertyDescription(ClassDescription classDescription, PropertyDescriptor propertyDescriptor, boolean updateForeignKeys, String reversePropertyName) {
	super(classDescription, propertyDescriptor, reversePropertyName);
	this.updateForeignKeys = updateForeignKeys;
}

public ReferencePropertyDescription(ClassDescription classDescription, PropertyDescriptor propertyDescriptor, boolean updateForeignKeys) {
	super(classDescription, propertyDescriptor);
	this.updateForeignKeys = updateForeignKeys;
}

public ClassDescription getReferencedClassDescription() {
	return getClassDescription().getSchemaDescription().getClassDescription(getPropertyDescriptor().getPropertyType().getName());
}

public boolean isUpdateForeignKeys() {
	return updateForeignKeys;
}

}
