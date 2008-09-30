package at.hts.persistence.schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassDescription {
	private final SchemaDescription schemaDescription;
	private final String className;
	private final ClassDescription superClassDescription;
	private final Map<String, PropertyDescription> propertyDescriptions = new HashMap<String, PropertyDescription>();
	private PropertyDescription id;

public ClassDescription(SchemaDescription schemaDescription, String className) {
	super();
	this.schemaDescription = schemaDescription;
	this.className = className;
	superClassDescription = null;
}

public ClassDescription(SchemaDescription schemaDescription, ClassDescription superClassDescription, String className) {
	super();
	this.schemaDescription = schemaDescription;
	this.className = className;
	this.superClassDescription = superClassDescription;
}

public void initialize() {
	if(superClassDescription != null) {
		id = superClassDescription.getId();
		for(PropertyDescription pd : superClassDescription.propertyDescriptions.values()) {
			propertyDescriptions.put(pd.getPropertyDescriptor().getName(), pd);
		}
	}
}

public Collection<PropertyDescription> getPropertyDescriptions() {
	return propertyDescriptions.values();
}

public SchemaDescription getSchemaDescription() {
	return schemaDescription;
}
	
public String getClassName() {
	return className;
}

public ClassDescription getSuperClassDescription() {
	return superClassDescription;
}

public PropertyDescription getPropertyDescription(String propertyName) {
	return propertyDescriptions.get(propertyName);
}

public void addPropertyDescription(PropertyDescription propertyDescription) {
	propertyDescriptions.put(propertyDescription.getPropertyDescriptor().getName(), propertyDescription);
}

public PropertyDescription getId() {
	return id;
}

public void setId(PropertyDescription id) {
	this.id = id;
}

public String toString() {
	return getClassName();
}

}
