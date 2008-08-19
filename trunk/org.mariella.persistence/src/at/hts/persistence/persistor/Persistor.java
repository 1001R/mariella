package at.hts.persistence.persistor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import at.hts.persistence.database.PreparedStatementBuilder;
import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.SchemaMapping;
import at.hts.persistence.runtime.MariellaPersistence;
import at.hts.persistence.runtime.Modifiable;
import at.hts.persistence.runtime.ModificationInfo;
import at.hts.persistence.runtime.ModificationTracker;

public class Persistor {
	private final SchemaMapping schemaMapping;
	private final ModificationTracker modificationTracker;
	private final DatabaseAccess databaseAccess;
	
	private Logger logger = MariellaPersistence.logger;

	private Map<Modifiable, ObjectPersistor> persistorMap = new HashMap<Modifiable, ObjectPersistor>();

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

public ObjectPersistor getObjectPersistor(Modifiable modifiable) {
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

public Long generateId() {
	return databaseAccess.generateId();
}

public void persist() {
	for(ModificationInfo modificationInfo : modificationTracker.getModifications()) {
		if(modificationInfo.getStatus() == ModificationInfo.Status.New) {
			getObjectPersistor(modificationInfo.getObject()).generateKey();
		}
	}

	for(ModificationInfo modificationInfo : modificationTracker.getModifications()) {
		if(modificationInfo.getStatus() != ModificationInfo.Status.NewRemoved) {
			getObjectPersistor(modificationInfo.getObject()).persist();
		}
	}

	modificationTracker.flushed();
}

public void execute(PreparedStatementBuilder psb) {
	getLogger().info(psb.getSqlDebugString());
	try {
		PreparedStatement ps = psb.createPreparedStatement(databaseAccess.getConnection());
		try {
			ps.executeUpdate();
		} finally {
			ps.close();
		}
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
