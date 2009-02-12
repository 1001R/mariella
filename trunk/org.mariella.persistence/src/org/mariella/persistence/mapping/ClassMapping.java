package org.mariella.persistence.mapping;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.Table;
import org.mariella.persistence.persistor.Row;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.schema.ClassDescription;


public abstract class ClassMapping extends AbstractClassMapping {
	
public ClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super(schemaMapping, classDescription);
}

public void initialize(InitializationContext context) {
}

public abstract Table getPrimaryTable();


public ColumnMapping getIdMapping() {
	return (ColumnMapping)getPropertyMapping(getClassDescription().getId());
}

public void collectUsedTables(Collection<Table> collection) {
	for(PropertyMapping pm : getPropertyMappings()) {
		pm.collectUsedTables(collection);
	}
}

public void collectUsedColumns(Collection<Column> collection) {
	for(PropertyMapping pm : getPropertyMappings()) {
		pm.collectUsedColumns(collection);
	}
}

public abstract TableReference join(SubSelectBuilder subSelectBuilder);

public void addObjectColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference) {
	for(PhysicalPropertyMapping pm : getPhysicalPropertyMappingList()) {
		pm.addColumns(subSelectBuilder, tableReference);
	}
}

public void addIdentityColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference) {
	getIdMapping().addColumns(subSelectBuilder, tableReference);
}


public Object createObject(ResultSetReader reader, ObjectFactory factory, boolean wantsObjects) throws SQLException {
	return createObject(reader, factory, wantsObjects, getPhysicalPropertyMappingList());
}

public Object createObject(ResultSetReader reader, ObjectFactory factory, boolean wantsObjects, List<PhysicalPropertyMapping> physicalPropertyMappings) throws SQLException {
	int columnIndex = reader.getCurrentColumnIndex();
	int idIndex = reader.getCurrentColumnIndex();
	if(wantsObjects) {
		idIndex += physicalPropertyMappings.indexOf(getIdMapping());
	} 
	reader.setCurrentColumnIndex(idIndex);
	Object identity = getIdMapping().getObject(reader, factory);
	Object entity = identity == null ? null : factory.getObject(this, identity);
	if(identity == null) {
		reader.setCurrentColumnIndex(reader.getCurrentColumnIndex() + (wantsObjects ? physicalPropertyMappings.size() : 1));
	} else {
		boolean update;
		if(entity != null ) {
			update = true;
		} else {
			update = false;
			entity = factory.createObject(this, identity);
		}
		if(wantsObjects) {
			reader.setCurrentColumnIndex(columnIndex);
			for(PhysicalPropertyMapping pm : physicalPropertyMappings) {
				if(pm == getIdMapping() || !getPhysicalPropertyMappingList().contains(pm)) {
					pm.advance(reader);
				} else {
					Object value = pm.getObject(reader, factory);
					if(update) {
						factory.updateValue(entity, pm.getPropertyDescription(), value);
					} else {
						factory.setValue(entity, pm.getPropertyDescription(), value);
					}
				}
			}
		}
	}
	
	return entity;
}

public Row createPrimaryRow() {
	return new Row(getPrimaryTable());
}

public String toString() {
	return getClassDescription().toString() + " (" + getPrimaryTable().getName() + ")"; 
}

public ColumnMapping getColumnMapping(Column column) {
	for(PhysicalPropertyMapping pm : physicalPropertyMappingList) {
		if(pm instanceof ColumnMapping) {
			return (ColumnMapping)pm;
		}
	}
	return null;
}

}