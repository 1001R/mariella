package org.mariella.persistence.mapping;

import org.mariella.persistence.database.DeleteStatementBuilder;
import org.mariella.persistence.database.InsertStatementBuilder;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;
import org.mariella.persistence.runtime.CollectionModificationInfo;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.schema.PropertyDescription;


public class CollectionAsTablePropertyMapping extends RelationshipAsTablePropertyMapping {

public CollectionAsTablePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription, String tableName, String foreignKeyToOwner, String foreignKeyToContent) {
	super(classMapping, propertyDescription, tableName, foreignKeyToOwner, foreignKeyToContent);
}

@Override
public void persist(ObjectPersistor persistor, Object value) {
	CollectionModificationInfo cmi = persistor.getModificationInfo().getCollectionModificationInfo(getPropertyDescription().getPropertyDescriptor().getName());
	if(cmi != null) {
		Object ownerIdentity = ModifiableAccessor.Singleton.getValue(persistor.getModificationInfo().getObject(), persistor.getClassMapping().getClassDescription().getId());
		for(Object added : cmi.getAdded()) {
			Object relatedIdentity = ModifiableAccessor.Singleton.getValue(added, getPropertyDescription().getReferencedClassDescription().getId());
			Row row = new Row(table);
			row.setProperty(foreignKeyToOwner, ownerIdentity);
			row.setProperty(foreignKeyToContent, relatedIdentity);
			InsertStatementBuilder isb = new InsertStatementBuilder(row);
			persistor.getPreparedStatementBuilders().add(isb);
		}
		for(Object removed: cmi.getRemoved()) {
			Object relatedIdentity = ModifiableAccessor.Singleton.getValue(removed , getPropertyDescription().getReferencedClassDescription().getId());
			Row row = new Row(table);
			row.setProperty(foreignKeyToOwner, ownerIdentity);
			row.setProperty(foreignKeyToContent, relatedIdentity);
			DeleteStatementBuilder dsb = new DeleteStatementBuilder(row);
			persistor.getPreparedStatementBuilders().add(dsb);
		}
	}
}

}
