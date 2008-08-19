package at.hts.persistence.mapping;

import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.schema.CollectionPropertyDescription;
import at.hts.persistence.schema.PropertyDescription;

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
