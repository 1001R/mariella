package at.hts.persistence.mapping;

import java.sql.SQLException;

import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.runtime.ModifiableAccessor;
import at.hts.persistence.schema.PropertyDescription;

public class EmbeddedPropertyMapping extends PhysicalPropertyMapping {
	protected final EmbeddedClassMapping embeddedClassMapping;
	
public EmbeddedPropertyMapping(ClassMapping classMapping, EmbeddedClassMapping embeddedClassMapping, PropertyDescription propertyDescription) {
	super(classMapping, propertyDescription);
	this.embeddedClassMapping = embeddedClassMapping;
}

@Override
public void addColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference) {
	for(PhysicalPropertyMapping pm : embeddedClassMapping.getPhysicalPropertyMappingList()) {
		pm.addColumns(subSelectBuilder, tableReference);
	}
}

@Override
public void advance(ResultSetReader reader) throws SQLException {
	for(PhysicalPropertyMapping pm : embeddedClassMapping.getPhysicalPropertyMappingList()) {
		pm.advance(reader);
	}
}

@Override
public Object getObject(ResultSetReader reader, ObjectFactory factory) throws SQLException {
	return embeddedClassMapping.createObject(reader, factory);
}

@Override
public void persist(ObjectPersistor persistor, Object value) {
	for(PhysicalPropertyMapping pm : embeddedClassMapping.getPhysicalPropertyMappingList()) {
		Object propertyValue = ModifiableAccessor.Singleton.getValue(value, pm.getPropertyDescription());
		pm.persist(persistor, propertyValue);
	}
}

}
