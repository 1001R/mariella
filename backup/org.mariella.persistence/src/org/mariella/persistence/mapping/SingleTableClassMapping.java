package org.mariella.persistence.mapping;

import java.util.Collection;

import org.mariella.persistence.database.Table;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.util.Util;


public class SingleTableClassMapping extends ClassMapping {
	protected Table primaryTable;

public SingleTableClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription, String tableName) {
	super(schemaMapping, classDescription);
	primaryTable = schemaMapping.getSchema().getTable(tableName);
	Util.assertTrue(primaryTable != null, "Unknown table");
}

protected SingleTableClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super(schemaMapping, classDescription);
}

@Override
public Table getPrimaryTable() {
	return primaryTable;
}

@Override
public void collectUsedTables(Collection<Table> collection) {
	if(!collection.contains(primaryTable)) {
		collection.add(primaryTable);
	}
	super.collectUsedTables(collection);
}

@Override
public TableReference join(SubSelectBuilder subSelectBuilder) {
	return subSelectBuilder.join(getPrimaryTable());
}

}
