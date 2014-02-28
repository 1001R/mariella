package org.mariella.persistence.persistor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.database.ConnectionCallback;
import org.mariella.persistence.database.PreparedStatementBuilder;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.IBatchStrategy;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.runtime.ModificationTracker;


public class Persistor {
	private final SchemaMapping schemaMapping;
	private final ModificationTracker modificationTracker;
	private final DatabaseAccess databaseAccess;

	private Map<Object, ObjectPersistor> persistorMap = new HashMap<Object, ObjectPersistor>();
	private IBatchStrategy batchStrategy;
	private PersistenceStatementsManager preparedStatementManager;

public Persistor(SchemaMapping schemaMapping, DatabaseAccess databaseAccess, ModificationTracker modificationTracker) {
	super();
	this.schemaMapping = schemaMapping;
	this.modificationTracker = modificationTracker;
	this.databaseAccess = databaseAccess;
	batchStrategy = schemaMapping.getDefaultBatchStrategy();
}

public void setBatchStrategy(IBatchStrategy batchStrategy) {
	this.batchStrategy = batchStrategy;
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

private ClassMapping addTablesToSuper(List<String> tables, ClassMapping classMapping) {
	if (classMapping.getSuperClassMapping() != null) {
		addTablesToSuper(tables, classMapping.getSuperClassMapping());
	}
	if (classMapping != null && classMapping.getMainUpdateTable() != null && !tables.contains(classMapping.getMainUpdateTable().getName())) {
		tables.add(classMapping.getMainUpdateTable().getName());
	}
	return classMapping.getSuperClassMapping() != null ? classMapping.getSuperClassMapping() : classMapping;
}

private void addInheritedTables(List<String> tables, ClassMapping classMapping) {
	if (classMapping != null && classMapping.getMainUpdateTable() != null && !tables.contains(classMapping.getMainUpdateTable().getName())) {
		tables.add(classMapping.getMainUpdateTable().getName());
	}
	for (ClassMapping childClass : classMapping.getImmediateChildren()) {
		addInheritedTables(tables, childClass);
	}
}

public void persist() {
	try {
		databaseAccess.doInConnection(new ConnectionCallback() {
			@Override
			public Object doInConnection(Connection connection) throws SQLException {
				Object[] orderedBatchedClasses = null;
				int maxBatchSize = 500;
				if (batchStrategy != null) {
					orderedBatchedClasses = batchStrategy.getOrderedBatchClasses();
					maxBatchSize = batchStrategy.getMaxBatchSize();
				}
				List<String> orderedBatchedTables = new ArrayList<String>();
				if (orderedBatchedClasses != null) {
					List<ClassMapping> classesToAddInheritance = new ArrayList<ClassMapping>();
					for (Object persistentClass : orderedBatchedClasses) {
						if (persistentClass instanceof String) {
							if (!orderedBatchedTables.contains(persistentClass)) {
								orderedBatchedTables.add((String) persistentClass);
							}
						} else {
							ClassMapping cm = schemaMapping.getClassMapping(((Class<?>) persistentClass).getName());
							// add the super class tables immediately when a class is processed
							ClassMapping superClass = addTablesToSuper(orderedBatchedTables, cm);
							if (!classesToAddInheritance.contains(superClass)) {
								classesToAddInheritance.add(superClass);
							}
						}
					}
					// add the derived classes after the given list of classes is processed
					for (ClassMapping cm : classesToAddInheritance) {
						addInheritedTables(orderedBatchedTables, cm);
					}
				}
				preparedStatementManager = new PersistenceStatementsManager(connection, maxBatchSize, orderedBatchedTables);
				
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

				try {
					preparedStatementManager.executeAll();
				} catch (SQLException ex) {
					try {
						preparedStatementManager.cloaseAll();
					} catch (SQLException ex2) {
						// ignore
					}
					throw ex;
				}

				for(ModificationInfo modificationInfo : new ArrayList<ModificationInfo>(modificationTracker.getModifications())) {
					if(modificationInfo.getStatus() != ModificationInfo.Status.NewRemoved) {
						getObjectPersistor(modificationInfo.getObject()).persistSecondary();
					}
				}

				try {
					preparedStatementManager.executeAll();
				} catch (SQLException ex) {
					try {
						preparedStatementManager.cloaseAll();
					} catch (SQLException ex2) {
						// ignore
					}
					throw ex;
				}
				preparedStatementManager = null;
				return null;
			}
		});
	} catch (SQLException ex) {
		throw new RuntimeException(ex);
	}
	modificationTracker.flushed();
}

public void execute(final PreparedStatementBuilder psb) {
	psb.initialize();
	psb.execute(preparedStatementManager);
	try {
		preparedStatementManager.executeBatchedIfMaxEntriesReached();
	} catch (SQLException ex) {
		throw new RuntimeException(ex);
	}
}

}
