package at.hts.persistence.persistor;

import java.util.ArrayList;
import java.util.List;

import at.hts.persistence.database.DeleteStatementBuilder;
import at.hts.persistence.database.InsertStatementBuilder;
import at.hts.persistence.database.PreparedStatementBuilder;
import at.hts.persistence.database.UpdateStatementBuilder;
import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.PropertyMapping;
import at.hts.persistence.runtime.ModifiableAccessor;
import at.hts.persistence.runtime.ModificationInfo;
import at.hts.persistence.schema.PropertyDescription;

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
			PropertyMapping propertyMapping = classMapping.getPropertyMapping(pd);
			Object value = ModifiableAccessor.Singleton.getValue(modificationInfo.getObject(), propertyMapping.getPropertyDescription());
			propertyMapping.persist(this, value);
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
