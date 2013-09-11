package org.mariella.oxygen.runtime.core;

import java.sql.Connection;
import java.sql.SQLException;

import org.mariella.persistence.mapping.SchemaMapping;

public interface OxySchemaMappingProvider {
	
	SchemaMapping provideSchemaMapping(Connection connection) throws SQLException;

}
