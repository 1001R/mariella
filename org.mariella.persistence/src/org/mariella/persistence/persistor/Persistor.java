package org.mariella.persistence.persistor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.mariella.persistence.database.ConnectionCallback;
import org.mariella.persistence.database.PreparedStatementBuilder;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.runtime.MariellaPersistence;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.runtime.ModificationTracker;


public class Persistor {
	private final SchemaMapping schemaMapping;
	private final ModificationTracker modificationTracker;
	private final DatabaseAccess databaseAccess;

	private Logger logger = MariellaPersistence.logger;

	private Map<Object, ObjectPersistor> persistorMap = new HashMap<Object, ObjectPersistor>();

public Persistor(SchemaMapping schemaMapping, DatabaseAccess databaseAccess, ModificationTracker modificationTracker) {
	super();
	this.schemaMapping = schemaMapping;
	this.modificationTracker = modificationTracker;
	this.databaseAccess = databaseAccess;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

public ModificationTracker getModificationTracker() {
	return modificationTracker;
}

public DatabaseAccess getDatabaseAccess() {
	return databaseAccess;
}

public ObjectPersistor getObjectPersistor(Object modifiable) {
	ObjectPersistor op = persistorMap.get(modifiable);
	if(op == null) {
		ClassMapping classMapping = schemaMapping.getClassMapping(modifiable.getClass().getName());
		op = new ObjectPersistor(this, classMapping, modificationTracker.getModificationInfo(modifiable));
		persistorMap.put(modifiable, op);
	}
	return op;
}

public Collection<ObjectPersistor> getObjectPersistors() {
	return persistorMap.values();
}

public void persist() {
	for(ModificationInfo modificationInfo : new ArrayList<ModificationInfo>(modificationTracker.getModifications())) {
		if(modificationInfo.getStatus() == ModificationInfo.Status.New) {
			getObjectPersistor(modificationInfo.getObject()).generateKey();
		}
	}

	for(ModificationInfo modificationInfo : new ArrayList<ModificationInfo>(modificationTracker.getModifications())) {
		if(modificationInfo.getStatus() != ModificationInfo.Status.NewRemoved) {
			getObjectPersistor(modificationInfo.getObject()).persistPrimary();
		}
	}

	for(ModificationInfo modificationInfo : new ArrayList<ModificationInfo>(modificationTracker.getModifications())) {
		if(modificationInfo.getStatus() != ModificationInfo.Status.NewRemoved) {
			getObjectPersistor(modificationInfo.getObject()).persistSecondary();
		}
	}

	modificationTracker.flushed();
}

public void execute(final PreparedStatementBuilder psb) {
	psb.initialize();
	getLogger().info(psb.getSqlDebugString());
	try {
		databaseAccess.doInConnection(
			new ConnectionCallback() {
				@Override
				public Object doInConnection(Connection connection) throws SQLException {
					psb.execute(connection);
					return null;
				}
			}
		);
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}
}

public Logger getLogger() {
	return logger;
}

public void setLogger(Logger logger) {
	this.logger = logger;
}

}
