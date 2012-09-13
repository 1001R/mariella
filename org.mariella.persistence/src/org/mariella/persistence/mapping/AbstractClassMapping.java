package org.mariella.persistence.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.PropertyDescription;


public abstract class AbstractClassMapping {
	protected final SchemaMapping schemaMapping;
	protected final ClassDescription classDescription;

	protected final Map<PropertyDescription, PropertyMapping> propertyMappings = new HashMap<PropertyDescription, PropertyMapping>();
	protected final List<PhysicalPropertyMapping> physicalPropertyMappingList = new ArrayList<PhysicalPropertyMapping>();
	protected final List<ColumnMapping> persistorGeneratedColumnMappings = new ArrayList<ColumnMapping>();

public AbstractClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super();
	this.schemaMapping = schemaMapping;
	this.classDescription = classDescription;
}

public void initialize(ClassMappingInitializationContext context) {
	if(getSuperClassMapping() != null) {
		context.ensureInitialized(getSuperClassMapping());
		propertyMappings.putAll(getSuperClassMapping().propertyMappings);
		physicalPropertyMappingList.addAll(0, getSuperClassMapping().physicalPropertyMappingList);
	}
	for(PhysicalPropertyMapping propertyMapping : physicalPropertyMappingList) {
		if(propertyMapping instanceof ColumnMapping) {
			ColumnMapping columnMapping = (ColumnMapping)propertyMapping;
			if(columnMapping.getValueGenerator() != null && !columnMapping.getValueGenerator().isGeneratedByDatabase()) {
				persistorGeneratedColumnMappings.add(columnMapping);
			}
		}
	}
}

public void postInitialize(ClassMappingInitializationContext context) {
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

public ClassDescription getClassDescription() {
	return classDescription;
}

public ClassMapping getSuperClassMapping() {
	if(getClassDescription().getSuperClassDescription() == null) {
		return null;
	} else {
		return getSchemaMapping().getClassMapping(getClassDescription().getSuperClassDescription().getClassName());
	}
}

protected List<PhysicalPropertyMapping> getPhysicalPropertyMappingList() {
	return physicalPropertyMappingList;
}

public List<ColumnMapping> getPersistorGeneratedColumnMappings() {
	return persistorGeneratedColumnMappings;
}

public PropertyMapping getPropertyMapping(PropertyDescription propertyDescription) {
	return propertyMappings.get(propertyDescription);
}

public PropertyMapping getPropertyMapping(String propertyName) {
	return getPropertyMapping(getClassDescription().getPropertyDescription(propertyName));
}

public Collection<PropertyMapping> getPropertyMappings() {
	return propertyMappings.values();
}

public void setPropertyMapping(PropertyDescription propertyDescription, PropertyMapping propertyMapping) {
	propertyMappings.put(propertyDescription, propertyMapping);
	if(propertyMapping instanceof PhysicalPropertyMapping) {
		PhysicalPropertyMapping ppm = (PhysicalPropertyMapping)propertyMapping;
		physicalPropertyMappingList.add(ppm);
	}
}

}
