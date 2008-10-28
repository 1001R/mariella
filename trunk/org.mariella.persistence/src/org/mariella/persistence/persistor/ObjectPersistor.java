package org.mariella.persistence.persistor;

import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.database.DeleteStatementBuilder;
import org.mariella.persistence.database.InsertStatementBuilder;
import org.mariella.persistence.database.PreparedStatementBuilder;
import org.mariella.persistence.database.UpdateStatementBuilder;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.PropertyMapping;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.schema.PropertyDescription;


public class ObjectPersistor {
	private final Persistor persistor;
	private final ClassMapping classMapping;
	private final ModificationInfo modificationInfo;
	private PreparedStatementBuilder primaryPreparedStatementBuilder = null;
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

public void persist() {
	if(modificationInfo.getStatus() == ModificationInfo.Status.New) {
		for(PropertyMapping propertyMapping : classMapping.getPropertyMappings()) {
			Object value = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), propertyMapping.getPropertyDescription());
			propertyMapping.persist(this, value);
		}
	} else if(modificationInfo.getStatus() == ModificationInfo.Status.Modified) {
		for(String propertyName : modificationInfo.getModifiedProperties()) {
			PropertyDescription pd = classMapping.getClassDescription().getPropertyDescription(propertyName);
			if(pd != null) {
				PropertyMapping propertyMapping = classMapping.getPropertyMapping(pd);
				Object value = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), propertyMapping.getPropertyDescription());
				propertyMapping.persist(this, value);
			}
		}
	} else if(modificationInfo.getStatus() == ModificationInfo.Status.Removed) {
		getPrimaryPreparedStatementBuilder();
	}
	if(primaryPreparedStatementBuilder != null) {
		preparedStatementBuilders.add(0, primaryPreparedStatementBuilder);
	}
	for(PreparedStatementBuilder psb : preparedStatementBuilders) {
		persistor.execute(psb);
	}
}

public void generateKey() {
	long key = persistor.generateId();
	ModifiableAccessor.Singleton.setValue(modificationInfo.getObject(), classMapping.getIdMapping().getPropertyDescription(), key);
}

public PreparedStatementBuilder getPrimaryPreparedStatementBuilder() {
	if(primaryPreparedStatementBuilder == null) {
		Row row = classMapping.createPrimaryRow();
		if(modificationInfo.getStatus() == ModificationInfo.Status.New) {
			primaryPreparedStatementBuilder = new InsertStatementBuilder(row);
		} else if(modificationInfo.getStatus() == ModificationInfo.Status.Removed) {
			primaryPreparedStatementBuilder = new DeleteStatementBuilder(row);
		} else if(modificationInfo.getStatus() == ModificationInfo.Status.Modified) {
			primaryPreparedStatementBuilder = new UpdateStatementBuilder(row);
		} else {
			throw new IllegalStateException();
		}
		Object identity = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), getClassMapping().getClassDescription().getId());
		classMapping.getIdMapping().persist(this, identity);
	}
	return primaryPreparedStatementBuilder;
}

}
