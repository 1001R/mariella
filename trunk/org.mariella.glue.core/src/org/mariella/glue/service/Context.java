package org.mariella.glue.service;

import java.sql.Connection;

import at.hts.persistence.loader.ClusterLoader;
import at.hts.persistence.loader.LoaderContext;
import at.hts.persistence.mapping.SchemaMapping;
import at.hts.persistence.persistor.ClusterDescription;
import at.hts.persistence.persistor.DatabaseAccess;
import at.hts.persistence.persistor.Persistor;
import at.hts.persistence.runtime.ModificationTracker;

public interface Context {

public ModificationTracker getModificationTracker();
public Persistor createPersistor(Connection connection);
public SchemaMapping getSchemaMapping();
public ClusterLoader createClusterLoader(ClusterDescription cd);
public LoaderContext createLoaderContext();
public DatabaseAccess createDatabaseAccess(final Connection connection);
}
