package org.mariella.persistence.mapping;

import java.util.Map;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.InsertStatementBuilder;
import org.mariella.persistence.database.SetColumnsiDeleteStatementBuilder;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;
import org.mariella.persistence.runtime.CollectionModificationInfo;
import org.mariella.persistence.schema.PropertyDescription;

public class CollectionAsTablePropertyMapping extends RelationshipAsOwnedTablePropertyMapping {

public CollectionAsTablePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription, String tableName, Map<Column, ColumnMapping> foreignKeyMapToOwner, Map<Column, ColumnMapping> foreignKeyMapToContent) {
	super(classMapping, propertyDescription, tableName, foreignKeyMapToOwner, foreignKeyMapToContent);
}

@Override
protected void persistPrimary(ObjectPersistor persistor, Object value) {
	CollectionModificationInfo cmi = persistor.getModificationInfo().getCollectionModificationInfo(getPropertyDescription().getPropertyDescriptor().getName());
	if(cmi != null) {
		for(Object removed: cmi.getRemoved()) {
			Row row = new Row(table);
			setRowValues(row, persistor.getModificationInfo().getObject(), removed);
			SetColumnsiDeleteStatementBuilder dsb = new SetColumnsiDeleteStatementBuilder(row);
			persistor.getPreparedStatementBuilders().add(dsb);
		}
	}
}

@Override
public void persistSecondary(ObjectPersistor persistor, Object value) {
	CollectionModificationInfo cmi = persistor.getModificationInfo().getCollectionModificationInfo(getPropertyDescription().getPropertyDescriptor().getName());
	if(cmi != null) {
		for(Object added : cmi.getAdded()) {
			Row row = new Row(table);
			setRowValues(row, persistor.getModificationInfo().getObject(), added);
			InsertStatementBuilder isb = new InsertStatementBuilder(row);
			persistor.getPreparedStatementBuilders().add(isb);
		}
	}
}

}
