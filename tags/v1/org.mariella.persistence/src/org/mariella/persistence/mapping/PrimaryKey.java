package org.mariella.persistence.mapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.schema.ClassDescription;

public class PrimaryKey {
	private final ClassMapping classMapping;
	private final ColumnMapping[] columnMappings;

	private final ColumnMapping[] generatedByDatabaseColumnMappings;
	private final String[] generatedByDatabaseColumnNames;

public PrimaryKey(ClassMapping classMapping, ColumnMapping[] columnMappings) {
	super();
	this.classMapping = classMapping;
	this.columnMappings = columnMappings;

	List<ColumnMapping> dbGeneratedColumns = new ArrayList<ColumnMapping>();
	List<String> dbGeneratedColumnNames = new ArrayList<String>();

	for(ColumnMapping columnMapping : columnMappings) {
		if(columnMapping.getValueGenerator() != null) {
			if(columnMapping.getValueGenerator().isGeneratedByDatabase()) {
				dbGeneratedColumns.add(columnMapping);
				dbGeneratedColumnNames.add(columnMapping.getUpdateColumn().getName());
			}
		}
	}
	generatedByDatabaseColumnMappings = dbGeneratedColumns.toArray(new ColumnMapping[dbGeneratedColumns.size()]);
	generatedByDatabaseColumnNames = dbGeneratedColumnNames.toArray(new String[dbGeneratedColumnNames.size()]);
}

public ColumnMapping[] getColumnMappings() {
	return columnMappings;
}

public ColumnMapping[] getGeneratedByDatabaseColumnMappings() {
	return generatedByDatabaseColumnMappings;
}

public String[] getGeneratedByDatabaseColumnNames() {
	return generatedByDatabaseColumnNames;
}

public void addColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference) {
	for(ColumnMapping columnMapping : columnMappings) {
		columnMapping.addColumns(subSelectBuilder, tableReference);
	}
}

public int getIndex(List<PhysicalPropertyMapping> physicalPropertyMappings) {
	return physicalPropertyMappings.indexOf(columnMappings[0]);
}

public Object getIdentity(ResultSetReader reader, ObjectFactory factory, ClassDescription classDescription ) throws SQLException {
	Map<String, Object> identityMap = new HashMap<String, Object>();
	identityMap.put(ClassDescription.TYPE_PROPERTY, classDescription.getClassName());
	if(classMapping.getClassDescription().getIdentityPropertyDescriptions().length == 0) {
		throw new IllegalStateException("No primary key columns defined for class " + classMapping.getClassDescription().getClassName());
	} else if(classMapping.getClassDescription().getIdentityPropertyDescriptions().length == 1) {
		Object value = columnMappings[0].getObject(reader, factory);
		if(value == null) {
			return null;
		} else {
			identityMap.put(columnMappings[0].getPropertyDescription().getPropertyDescriptor().getName(), value);
		}
	} else if(classMapping.getClassDescription().getIdentityClass() != null) {
		throw new UnsupportedOperationException("Identity class is not supported!");
	} else {
		for(ColumnMapping columnMapping : columnMappings) {
			Object value = columnMapping.getObject(reader, factory);
			if(value == null) {
				return null;
			} else {
				identityMap.put(columnMapping.getPropertyDescription().getPropertyDescriptor().getName(), value);
			}
		}
	}
	return identityMap;
}

public boolean contains(PropertyMapping pm) {
	for(ColumnMapping cm : columnMappings) {
		if(cm == pm) {
			return true;
		}
	}
	return false;
}

}
