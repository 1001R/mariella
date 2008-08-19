package at.hts.persistence.mapping;

import java.sql.SQLException;

import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.schema.PropertyDescription;

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
