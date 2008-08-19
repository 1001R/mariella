package at.hts.persistence.mapping;

import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.schema.PropertyDescription;

public class ReferenceAsTablePropertyMapping extends RelationshipAsTablePropertyMapping {

public ReferenceAsTablePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription, String tableName, String foreignKeyToOwner, String foreignKeyToContent) {
	super(classMapping, propertyDescription, tableName, foreignKeyToOwner, foreignKeyToContent);
}

public ReferenceAsTablePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription) {
	super(classMapping, propertyDescription);
}

@Override
public void persist(ObjectPersistor persistor, Object value) {
	throw new UnsupportedOperationException();
}

}
