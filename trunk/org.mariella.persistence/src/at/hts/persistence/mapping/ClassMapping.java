package at.hts.persistence.mapping;

import java.sql.SQLException;
import java.util.Collection;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Table;
import at.hts.persistence.persistor.Row;
import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.schema.ClassDescription;

public abstract class ClassMapping extends AbstractClassMapping {
	
public ClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super(schemaMapping, classDescription);
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
	int columnIndex = reader.getCurrentColumnIndex();
	int idIndex = reader.getCurrentColumnIndex();
	if(wantsObjects) {
		idIndex += getPhysicalPropertyMappingList().indexOf(getIdMapping());
	} 
	reader.setCurrentColumnIndex(idIndex);
	Object identity = getIdMapping().getObject(reader, factory);
	Object entity = identity == null ? null : factory.getObject(this, identity);
	if(identity == null) {
		reader.setCurrentColumnIndex(reader.getCurrentColumnIndex() + (wantsObjects ? getPhysicalPropertyMappingList().size() : 1));
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
			for(PhysicalPropertyMapping pm : getPhysicalPropertyMappingList()) {
				if(pm == getIdMapping()) {
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

}