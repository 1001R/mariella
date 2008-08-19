package at.hts.persistence.mapping;

import at.hts.persistence.database.DeleteStatementBuilder;
import at.hts.persistence.database.InsertStatementBuilder;
import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.persistor.Row;
import at.hts.persistence.runtime.CollectionModificationInfo;
import at.hts.persistence.runtime.ModifiableAccessor;
import at.hts.persistence.schema.PropertyDescription;

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
