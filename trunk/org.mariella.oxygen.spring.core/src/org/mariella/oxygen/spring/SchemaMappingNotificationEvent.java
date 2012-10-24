package org.mariella.oxygen.spring;

import org.mariella.persistence.mapping.SchemaMapping;
import org.springframework.context.ApplicationEvent;

public class SchemaMappingNotificationEvent extends ApplicationEvent {
private static final long serialVersionUID = 1L;

private String persistenceUnitName;
private SchemaMapping schemaMapping;

public SchemaMappingNotificationEvent(Object source, String persistenceUnitName, SchemaMapping schemaMapping) {
	super(source);
	this.persistenceUnitName = persistenceUnitName;
	this.schemaMapping = schemaMapping;
}

public String getPersistenceUnitName() {
	return persistenceUnitName;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}


}
