package at.hts.persistence.mapping;

import java.sql.SQLException;
import java.util.Collection;

import at.hts.persistence.database.Column;
import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.util.Util;

public class ColumnMapping extends PhysicalPropertyMapping {
	private final Column column;

public ColumnMapping(ClassMapping classMapping, PropertyDescription propertyDescription, String columnName) {
	super(classMapping, propertyDescription);
	column = classMapping.getPrimaryTable().getColumn(columnName);
	Util.assertTrue(column != null, "Unknown column");
}

public ColumnMapping(AbstractClassMapping classMapping, PropertyDescription propertyDescription, Column column) {
	super(classMapping, propertyDescription);
	this.column = column;
	Util.assertTrue(column != null, "Unknown column");
}

public Column getColumn() {
	return column;
}

@Override
public void persist(ObjectPersistor persistor, Object value) {
	persistor.getPrimaryPreparedStatementBuilder().getRow().setProperty(getColumn(), value);
}

@Override
public void collectUsedColumns(Collection<Column> collection) {
	collection.add(column);
}

@Override
public <T> Object getObject(ResultSetReader reader, ObjectFactory factory) throws SQLException {
	Object value = column.getObject(reader.getResultSet(), reader.getCurrentColumnIndex());
	advance(reader);
	return value;
}

@Override
public void advance(ResultSetReader reader) throws SQLException {
	reader.setCurrentColumnIndex(reader.getCurrentColumnIndex() + 1);
}

@Override
public void addColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference) {
	subSelectBuilder.addSelectItem(tableReference, getColumn());	
}
}
