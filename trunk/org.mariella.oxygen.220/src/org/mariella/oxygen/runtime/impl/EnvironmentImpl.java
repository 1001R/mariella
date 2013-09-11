package org.mariella.oxygen.runtime.impl;

import java.lang.reflect.Constructor;
import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.logging.Logger;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.runtime.core.OxyConnectionProvider;
import org.mariella.persistence.annotations.mapping_builder.DatabaseInfoProvider;
import org.mariella.persistence.annotations.mapping_builder.DatabaseMetaDataDatabaseInfoProvider;
import org.mariella.persistence.annotations.mapping_builder.PersistenceBuilder;
import org.mariella.persistence.generic.GenericPersistenceBuilder;
import org.mariella.persistence.mapping.OxyUnitInfo;
import org.mariella.persistence.mapping.SchemaMapping;

public abstract class EnvironmentImpl implements Environment {
	protected Logger logger = Logger.getLogger(EnvironmentImpl.class.getName());
	
	protected ClassResolver persistenceClassResolver;
	protected OxyUnitInfo oxyUnitInfo;
	protected Map<?, ?> properties;
	private SchemaMapping schemaMapping;

protected void createSchemaMapping() {
	try {
		OxyConnectionProvider connectionProvider = createConnectionProvider();
		try {
			DatabaseMetaData metaData = connectionProvider.getConnection().getMetaData();
			DatabaseMetaDataDatabaseInfoProvider databaseInfoProvider = new DatabaseMetaDataDatabaseInfoProvider(metaData);
			Boolean ignoreSchema = getBooleanProperty(IGNORE_DB_SCHEMA);
			if(ignoreSchema != null) {
				databaseInfoProvider.setIgnoreSchema(ignoreSchema);
			}
			Boolean ignoreCatalog = getBooleanProperty(IGNORE_DB_CATALOG);
			if(ignoreCatalog != null) {
				databaseInfoProvider.setIgnoreCatalog(ignoreCatalog);
			}
			initializeMapping(databaseInfoProvider);
		} finally {
			connectionProvider.close();
		}
	} catch(Exception e) {
		throw new IllegalStateException("Unable to create schema mapping", e);
	}
}
	
protected void initializeMapping(DatabaseInfoProvider databaseInfoProvider) {
	String persistenceBuilderClassName = getStringProperty(PERSISTENCE_BUILDER);
	PersistenceBuilder persistenceBuilder;
	if(persistenceBuilderClassName != null) {
		try {
			Class<?> persistenceBuilderClass = persistenceClassResolver.resolveClass(persistenceBuilderClassName);
			Constructor<?> constructor = persistenceBuilderClass.getConstructor(OxyUnitInfo.class, DatabaseInfoProvider.class);
			persistenceBuilder = (PersistenceBuilder)constructor.newInstance(oxyUnitInfo, databaseInfoProvider);
		} catch(Exception e) {
			throw new RuntimeException(e);
		} 
	} else {
		persistenceBuilder = new GenericPersistenceBuilder(oxyUnitInfo, databaseInfoProvider);
	}
	
	configurePersistenceBuilder(persistenceBuilder);
	
	persistenceBuilder.build();
	schemaMapping = persistenceBuilder.getPersistenceInfo().getSchemaMapping();
}

protected void configurePersistenceBuilder(PersistenceBuilder persistenceBuilder) {
	
}

public OxyUnitInfo getOxyUnitInfo() {
	return oxyUnitInfo;
}

public ClassResolver getPersistenceClassResolver() {
	return persistenceClassResolver;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

protected Boolean getBooleanProperty(String propertyName) {
	Boolean value = getBooleanProperty(properties, propertyName);
	return value == null ? getBooleanProperty(oxyUnitInfo.getProperties(), propertyName) : value;
}

protected Boolean getBooleanProperty(Map<?, ?> properties, String propertyName) {
	if(properties.get(propertyName) == null) {
		return null;
	} else if(properties.get(propertyName).equals("true")) {
		return true;
	} else if(properties.get(propertyName).equals("false")) {
		return false;
	} else {
		logger.severe("Invalid value for boolean property + '" + oxyUnitInfo.getProperties().getProperty(propertyName) + "'. Value will be ignored!");
		return null;
	}
}

protected String getStringProperty(String propertyName) {
	String value = getStringProperty(properties, propertyName);
	return value == null ? getStringProperty(oxyUnitInfo.getProperties(), propertyName) : value;
}

protected String getStringProperty(Map<?, ?> properties, String propertyName) {
	return (String)properties.get(propertyName);
}

protected Object getProperty(String propertyName) {
	Object value = getProperty(properties, propertyName);
	return value == null ? getProperty(oxyUnitInfo.getProperties(), propertyName) : value;
}

protected Object getProperty(Map<?, ?> properties, String propertyName) {
	return properties.get(propertyName);
}

}
