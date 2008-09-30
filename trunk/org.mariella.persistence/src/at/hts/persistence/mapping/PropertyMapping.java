package at.hts.persistence.mapping;

import java.util.Collection;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Table;
import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.schema.PropertyDescription;

public abstract class PropertyMapping {
	protected final PropertyDescription propertyDescription;
	protected final Class<?> type;
	protected final AbstractClassMapping classMapping;
	
public PropertyMapping(AbstractClassMapping classMapping, PropertyDescription propertyDescription) {
	super();
	this.classMapping = classMapping;
	this.propertyDescription = propertyDescription;
	if(classMapping == null || propertyDescription == null) {
		throw new IllegalArgumentException();
	}
	this.type = null;
}

public PropertyMapping(ClassMapping classMapping, Class<?> type) {
	super();
	this.classMapping = classMapping;
	this.propertyDescription = null;
	if(classMapping == null || propertyDescription == null) {
		throw new IllegalArgumentException();
	}
	this.type = type;
}

public PropertyDescription getPropertyDescription() {
	return propertyDescription;
}

public Class<?> getType() {
	return type != null ? type : propertyDescription.getPropertyDescriptor().getPropertyType();
}

public AbstractClassMapping getClassMapping() {
	return classMapping;
}

public abstract void persist(ObjectPersistor persistor, Object value);

public void collectUsedTables(Collection<Table> collection) {
}

public void collectUsedColumns(Collection<Column> collection) {
}

public String toString() {
	return getClassMapping().toString() + "." + getPropertyDescription().getPropertyDescriptor().getName();
}

}
