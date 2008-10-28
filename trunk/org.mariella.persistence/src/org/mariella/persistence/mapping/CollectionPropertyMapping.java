package org.mariella.persistence.mapping;

import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.schema.CollectionPropertyDescription;
import org.mariella.persistence.schema.PropertyDescription;


public class CollectionPropertyMapping extends RelationshipPropertyMapping {

public CollectionPropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription) {
	super(classMapping, (CollectionPropertyDescription)propertyDescription);
}

@Override
public CollectionPropertyDescription getPropertyDescription() {
	return (CollectionPropertyDescription)super.getPropertyDescription();
}

public TableReference join(SubSelectBuilder subSelectBuilder, TableReference myTableReference) {
	return getReversePropertyMapping().joinReverse(subSelectBuilder, myTableReference);
}

@Override
public TableReference joinReverse(SubSelectBuilder subSelectBuilder, TableReference referencedTableReference) {
	throw new UnsupportedOperationException();
}

@Override
public void persist(ObjectPersistor persistor, Object value) {
}

}
