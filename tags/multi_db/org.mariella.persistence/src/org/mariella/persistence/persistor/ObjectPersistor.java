package org.mariella.persistence.persistor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.database.PreparedStatementBuilder;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.ColumnMapping;
import org.mariella.persistence.mapping.PropertyMapping;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.schema.PropertyDescription;


public class ObjectPersistor {
	private final Persistor persistor;
	private final ClassMapping classMapping;
	private final ModificationInfo modificationInfo;
	private Map<Object, SingleRowPreparedStatementBuilder> primaryPreparedStatementBuilderMap = new HashMap<Object, SingleRowPreparedStatementBuilder>();
	private List<SingleRowPreparedStatementBuilder> primaryPreparedStatementBuilders = new ArrayList<SingleRowPreparedStatementBuilder>();
	private List<PreparedStatementBuilder> preparedStatementBuilders = new ArrayList<PreparedStatementBuilder>();

public ObjectPersistor(Persistor persistor, ClassMapping classMapping, ModificationInfo modificationInfo) {
	super();
	this.persistor = persistor;
	this.classMapping = classMapping;
	this.modificationInfo = modificationInfo;
}

public ModificationInfo getModificationInfo() {
	return modificationInfo;
}

public ClassMapping getClassMapping() {
	return classMapping;
}

public Persistor getPersistor() {
	return persistor;
}

public List<PreparedStatementBuilder> getPreparedStatementBuilders() {
	return preparedStatementBuilders;
}

public SingleRowPreparedStatementBuilder getPrimaryPreparedStatementBuilder(Object key) {
	SingleRowPreparedStatementBuilder primaryPreparedStatementBuilder = primaryPreparedStatementBuilderMap.get(key);
	if(primaryPreparedStatementBuilder == null) {
		primaryPreparedStatementBuilder = classMapping.createPrimaryPreparedStatementBuilder(this, key);
		primaryPreparedStatementBuilderMap.put(key, primaryPreparedStatementBuilder);
		primaryPreparedStatementBuilders.add(primaryPreparedStatementBuilder);
		classMapping.initializePrimaryPreparedStatementBuilder(this, key, primaryPreparedStatementBuilder);
	}
	return primaryPreparedStatementBuilder;
}

public void persistPrimary() {
	classMapping.createInitialPrimaryPreparedStatementBuilders(this);
	if(modificationInfo.getStatus() == ModificationInfo.Status.New) {
		for(PropertyMapping propertyMapping : classMapping.getPropertyMappings()) {
			if(propertyMapping.isInsertable()) {
				Object value = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), propertyMapping.getPropertyDescription());
				propertyMapping.insertPrimary(this, value);
			}
		}
	} else if(modificationInfo.getStatus() == ModificationInfo.Status.Modified) {
		for(String propertyName : modificationInfo.getModifiedProperties()) {
			PropertyDescription pd = classMapping.getClassDescription().getPropertyDescription(propertyName);
			if(pd != null) {
				PropertyMapping propertyMapping = classMapping.getPropertyMapping(pd);
				if(propertyMapping.isUpdatable()) {
					Object value = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), propertyMapping.getPropertyDescription());
					propertyMapping.updatePrimary(this, value);
				}
			}
		}
	}

	preparedStatementBuilders.addAll(0, primaryPreparedStatementBuilders);
	for(PreparedStatementBuilder psb : preparedStatementBuilders) {
		persistor.execute(psb);
	}
}

public void persistSecondary() {
	preparedStatementBuilders = new ArrayList<PreparedStatementBuilder>();
	if(modificationInfo.getStatus() == ModificationInfo.Status.New) {
		for(PropertyMapping propertyMapping : classMapping.getPropertyMappings()) {
			if(propertyMapping.isInsertable()) {
				Object value = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), propertyMapping.getPropertyDescription());
				propertyMapping.insertSecondary(this, value);
			}
		}
	} else if(modificationInfo.getStatus() == ModificationInfo.Status.Modified) {
		for(String propertyName : modificationInfo.getModifiedProperties()) {
			PropertyDescription pd = classMapping.getClassDescription().getPropertyDescription(propertyName);
			if(pd != null) {
				PropertyMapping propertyMapping = classMapping.getPropertyMapping(pd);
				if(propertyMapping.isUpdatable()) {
					Object value = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), propertyMapping.getPropertyDescription());
					propertyMapping.updateSecondary(this, value);
				}
			}
		}
	}
	for(PreparedStatementBuilder psb : preparedStatementBuilders) {
		persistor.execute(psb);
	}
}

public void generateKey() {
	for(ColumnMapping columnMapping : classMapping.getPersistorGeneratedColumnMappings()) {
		try {
			Object value = columnMapping.getValueGenerator().generate(persistor.getDatabaseAccess());
			ModifiableAccessor.Singleton.setValue(modificationInfo.getObject(), columnMapping.getPropertyDescription(), value);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}

public ModifiableAccessor getModifiableAccessor() {
	return ModifiableAccessor.Singleton;
}

}
