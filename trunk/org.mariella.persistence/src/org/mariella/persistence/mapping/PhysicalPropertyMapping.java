package org.mariella.persistence.mapping;

import java.sql.SQLException;

import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.schema.PropertyDescription;


public abstract class PhysicalPropertyMapping extends PropertyMapping {

public PhysicalPropertyMapping(AbstractClassMapping classMapping, PropertyDescription propertyDescription) {
	super(classMapping, propertyDescription);
}

@Override
public abstract void persist(ObjectPersistor persistor, Object value);
public abstract <T> Object getObject(ResultSetReader reader, ObjectFactory factory) throws SQLException;
public abstract void advance(ResultSetReader reader) throws SQLException;
public abstract void addColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference);
}
