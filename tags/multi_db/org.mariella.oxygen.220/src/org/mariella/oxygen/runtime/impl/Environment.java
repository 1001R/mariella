package org.mariella.oxygen.runtime.impl;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.runtime.core.OxyConnectionProvider;
import org.mariella.persistence.annotations.mapping_builder.DatabaseMetaDataDatabaseInfoProvider;
import org.mariella.persistence.mapping.SchemaMapping;

public interface Environment {
	public static final String PERSISTENCE_BUILDER = "org.mariella.oxygen.persistenceBuilder";
	public static final String IGNORE_DB_SCHEMA = DatabaseMetaDataDatabaseInfoProvider.class.getName() + ".ignoreSchema";
	public static final String IGNORE_DB_CATALOG = DatabaseMetaDataDatabaseInfoProvider.class.getName() + ".ignoreCatalog";

public SchemaMapping getSchemaMapping();
public ClassResolver getPersistenceClassResolver();
public OxyConnectionProvider createConnectionProvider();
public OxyEntityTransactionFactory createEntityTransactionFactory();

public void initialize(String emName);
}
