package org.mariella.persistence.mapping;

import java.sql.SQLException;

import org.mariella.persistence.schema.ClassDescription;


public class EmbeddedClassMapping extends AbstractClassMapping {
	
public EmbeddedClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super(schemaMapping, classDescription);
}

public Object createObject(ResultSetReader reader, ObjectFactory factory) throws SQLException {
	int columnIndex = reader.getCurrentColumnIndex();
	Object entity = factory.createEmbeddableObject(this);
	reader.setCurrentColumnIndex(columnIndex);
	for(PhysicalPropertyMapping pm : getPhysicalPropertyMappingList()) {
		Object value = pm.getObject(reader, factory);
		factory.setValue(entity, pm.getPropertyDescription(), value);
	}
	return entity;
}

}
