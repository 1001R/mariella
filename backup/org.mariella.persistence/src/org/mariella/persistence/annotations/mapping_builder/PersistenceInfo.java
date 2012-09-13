package org.mariella.persistence.annotations.mapping_builder;

import org.mariella.persistence.database.Schema;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.schema.SchemaDescription;


public class PersistenceInfo {
	private Schema schema;
	private SchemaMapping schemaMapping;
	private SchemaDescription schemaDescription;
	
public PersistenceInfo() {
	schema = new Schema();
	schemaDescription = new SchemaDescription();
	schemaMapping = new SchemaMapping(schemaDescription, schema);
	
}

public Schema getSchema() {
	return schema;
}

public void setSchema(Schema schema) {
	this.schema = schema;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

public void setSchemaMapping(SchemaMapping schemaMapping) {
	this.schemaMapping = schemaMapping;
}

public SchemaDescription getSchemaDescription() {
	return schemaDescription;
}

public void setSchemaDescription(SchemaDescription schemaDescription) {
	this.schemaDescription = schemaDescription;
}
}
