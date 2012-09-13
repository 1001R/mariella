package org.mariella.persistence.mapping;

import java.util.Map;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.Table;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.schema.PropertyDescription;

public class ToManyPropertyMapping extends RelationshipAsTablePropertyMapping {
	
public ToManyPropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription) {
	super(classMapping, propertyDescription);
}

@Override
public RelationshipAsOwnedTablePropertyMapping getReversePropertyMapping() {
	return (RelationshipAsOwnedTablePropertyMapping)super.getReversePropertyMapping();
}

@Override
public Map<Column, ColumnMapping> getForeignKeyMapToContent() {
	return getReversePropertyMapping().getForeignKeyMapToOwner();
}

@Override
public Map<Column, ColumnMapping> getForeignKeyMapToOwner() {
	return getReversePropertyMapping().getForeignKeyMapToContent();
}

@Override
public Table getTable() {
	return getReversePropertyMapping().getTable();
}

@Override
protected void persistPrimary(ObjectPersistor persistor, Object value) {
}

@Override
protected void persistSecondary(ObjectPersistor persistor, Object value) {
}

}
