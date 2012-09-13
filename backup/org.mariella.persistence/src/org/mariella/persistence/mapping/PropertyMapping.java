package org.mariella.persistence.mapping;

import java.util.Collection;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.Table;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.schema.PropertyDescription;


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

public void insert(ObjectPersistor persistor, Object value) {
	persist(persistor, value);
}

public void update(ObjectPersistor persistor, Object value) {
	persist(persistor, value);
}

protected abstract void persist(ObjectPersistor persistor, Object value);

public void collectUsedTables(Collection<Table> collection) {
}

public void collectUsedColumns(Collection<Column> collection) {
}

public String toString() {
	return getClassMapping().toString() + "." + getPropertyDescription().getPropertyDescriptor().getName();
}

}
