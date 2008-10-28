package org.mariella.glue.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.glue.core.Activator;
import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.Table;
import org.mariella.persistence.loader.ClusterLoader;
import org.mariella.persistence.loader.LoaderContext;
import org.mariella.persistence.loader.ModifiableFactoryImpl;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.persistor.DatabaseAccess;
import org.mariella.persistence.persistor.Persistor;
import org.mariella.persistence.runtime.ModificationTracker;
import org.mariella.persistence.runtime.RIListener;


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
