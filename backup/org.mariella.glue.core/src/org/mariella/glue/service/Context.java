package org.mariella.glue.service;

import java.sql.Connection;

import org.mariella.persistence.loader.ClusterLoader;
import org.mariella.persistence.loader.LoaderContext;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.persistor.DatabaseAccess;
import org.mariella.persistence.persistor.Persistor;
import org.mariella.persistence.runtime.ModificationTracker;


public interface Context {

public ModificationTracker getModificationTracker();
public Persistor createPersistor(Connection connection);
public SchemaMapping getSchemaMapping();
public ClusterLoader createClusterLoader(ClusterDescription cd);
public LoaderContext createLoaderContext();
public DatabaseAccess createDatabaseAccess(final Connection connection);
public String getUserName();
public boolean isUserInRole(String role);
}
