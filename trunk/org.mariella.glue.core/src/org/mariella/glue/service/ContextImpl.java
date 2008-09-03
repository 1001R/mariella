package org.mariella.glue.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.glue.core.Activator;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Table;
import at.hts.persistence.loader.ClusterLoader;
import at.hts.persistence.loader.LoaderContext;
import at.hts.persistence.loader.ModifiableFactoryImpl;
import at.hts.persistence.mapping.SchemaMapping;
import at.hts.persistence.persistor.ClusterDescription;
import at.hts.persistence.persistor.DatabaseAccess;
import at.hts.persistence.persistor.Persistor;
import at.hts.persistence.runtime.ModificationTracker;
import at.hts.persistence.runtime.RIListener;

public abstract class ContextImpl implements Context {
	protected final SchemaMapping schemaMapping;
	protected final ModificationTracker  modificationTracker;
	protected final ClassLoader classLoader;
	
	private Long nextId = null;
	private int sequenceIncrement = 100;
	private int increment = 0;

public ContextImpl(SchemaMapping schemaMapping, ClassLoader classLoader) {
	super();
	this.schemaMapping = schemaMapping;
	this.classLoader = classLoader;
	modificationTracker = new ModificationTracker();
	modificationTracker.addListener(new RIListener(schemaMapping.getSchemaDescription()));
}
	
public Persistor createPersistor(Connection connection) {
	Persistor persistor = new Persistor(schemaMapping, createDatabaseAccess(connection), modificationTracker);
	persistor.setLogger(Activator.logger);
	return persistor;
}

public ModificationTracker getModificationTracker() {
	return modificationTracker;
}

public ClusterLoader createClusterLoader(ClusterDescription cd) {
	return new ClusterLoader(getSchemaMapping(), cd);
}

public LoaderContext createLoaderContext() {
	LoaderContext lc = new LoaderContext(modificationTracker, new ModifiableFactoryImpl(classLoader));
	lc.setLogger(Activator.logger);
	return lc;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

public final DatabaseAccess createDatabaseAccess(final Connection connection) {
	return new DatabaseAccess() {
		public Connection getConnection() throws SQLException {
			return connection;
		}
		public long generateId() {
			if(nextId == null || increment == sequenceIncrement) {
				try {
					String sql = "SELECT IDSEQUENCE.NEXTVAL FROM DUAL";
					Activator.logger.info(sql);
					PreparedStatement ps = getConnection().prepareStatement(sql);
					try {
						ResultSet rs = ps.executeQuery();
						try {
							rs.next();
							nextId = rs.getLong(1);
						} finally {
							rs.close();
						}
					} finally {
						ps.close();
					}
				} catch(SQLException e) {
					throw new RuntimeException(e);
				}
				increment = 0;
			}
			long result = nextId.longValue() + increment;
			increment++;
			return result;
		}
	};
}

public Column getColumn(String tableName, String columnName) {
	Table table = getTable(tableName);
	return table == null ? null : table.getColumn(columnName);
}

public Table getTable(String tableName) {
	return schemaMapping.getSchema().getTable(tableName);
}

}
