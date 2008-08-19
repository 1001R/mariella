package at.hts.persistence.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;

public class AbstractClassMapping {
	protected final SchemaMapping schemaMapping;
	protected final ClassDescription classDescription;

	protected Map<PropertyDescription, PropertyMapping> propertyMappings = new HashMap<PropertyDescription, PropertyMapping>();
	protected List<PhysicalPropertyMapping> physicalPropertyMappingList = new ArrayList<PhysicalPropertyMapping>();

public AbstractClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super();
	this.schemaMapping = schemaMapping;
	this.classDescription = classDescription;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

public ClassDescription getClassDescription() {
	return classDescription;
}

protected List<PhysicalPropertyMapping> getPhysicalPropertyMappingList() {
	return physicalPropertyMappingList;
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
