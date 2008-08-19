package at.hts.persistence.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Schema;
import at.hts.persistence.database.Table;
import at.hts.persistence.schema.SchemaDescription;

public class SchemaMapping {
	protected final SchemaDescription schemaDescription;
	protected final Schema schema;
	private Map<String, ClassMapping> classMappingMap = new HashMap<String, ClassMapping>();

public SchemaMapping(SchemaDescription schemaDescription, Schema schema) {
	super();
	this.schemaDescription = schemaDescription;
	this.schema = schema;
}

public SchemaDescription getSchemaDescription() {
	return schemaDescription;
}

public Schema getSchema() {
	return schema;
}

public ClassMapping getClassMapping(String className) {
	return classMappingMap.get(className);
}

public void setClassMapping(String className, ClassMapping tableMapping) {
	classMappingMap.put(className, tableMapping);
}

public Collection<ClassMapping> getClassMappings() {
	return classMappingMap.values();
}

public Collection<Table> getUsedTables() {
	Set<Table> used = new HashSet<Table>();
	for(ClassMapping cm : getClassMappings()) {
		cm.collectUsedTables(used);
	}
	return used;
}

public Collection<Column> getUsedColumns() { 
	Set<Column> used = new HashSet<Column>();
	for(ClassMapping cm : getClassMappings()) {
		cm.collectUsedColumns(used);
	}
	return used;
}

public Collection<Table> getUnusedTables() {
	Collection<Table> unused= new HashSet<Table>(schema.getTables());
	unused.removeAll(getUsedTables());
	return unused;
}

public Collection<Column> getUnusedColumns() {
	Collection<Column> unused= new HashSet<Column>();
	for(Table table : getUsedTables()) {
		unused.addAll(table.getColumns());
	}
	unused.removeAll(getUsedColumns());
	return unused;
}

}
