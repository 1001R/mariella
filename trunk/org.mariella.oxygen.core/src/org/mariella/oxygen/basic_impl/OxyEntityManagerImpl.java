package org.mariella.oxygen.basic_impl;


import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.persistence.runtime.RIListener;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.SchemaDescription;

public abstract class OxyEntityManagerImpl implements OxyEntityManager {

	protected OxyObjectPoolImpl objectPool;
	private SchemaDescription schemaDescription;
	private final ClassResolver entityClassResolver;

public OxyEntityManagerImpl(SchemaDescription schemaDescription) {
	super();
	this.entityClassResolver = new DefaultClassResolver(getClass().getClassLoader());
	this.schemaDescription = schemaDescription;
	createObjectPool();
}

public OxyEntityManagerImpl(ClassResolver entityClassResolver, SchemaDescription schemaDescription) {
	super();
	this.schemaDescription = schemaDescription;
	this.entityClassResolver = entityClassResolver;
	createObjectPool();
}

private void createObjectPool() {
	EntityManagerModificationTracker modificationTracker = new EntityManagerModificationTracker();
	modificationTracker.setEntityManager(this);
	modificationTracker.setInfo("Created by " + OxyEntityManagerImpl.this.getClass().getName());
	modificationTracker.addPersistentListener(new RIListener(modificationTracker));
	OxyObjectPoolImpl objectPool = new OxyObjectPoolImpl(modificationTracker);
	objectPool.setInfo("Created by " + OxyEntityManagerImpl.this.getClass().getName());
	setObjectPool(objectPool);
}

public SchemaDescription getSchemaDescription() {
	return schemaDescription;
}

public ClassResolver getEntityClassResolver() {
	return entityClassResolver;
}

public ClassDescription getClassDescription(Class<?> entityClass) {
	return schemaDescription.getClassDescription(entityClass.getName());
}

public Class<?> getClass(ClassDescription classDescription) {
	try {
//		return getEntityClassLoader() == null ? Class.forName(classDescription.getClassName()) : Class.forName(classDescription.getClassName(), true, entityClassLoader);
		return getEntityClassResolver().resolveClass(classDescription.getClassName());
	} catch(ClassNotFoundException e) {
		throw createRuntimeException(e);
	}
}

public OxyObjectPool getObjectPool() {
	return objectPool;
}

public void setObjectPool(OxyObjectPool objectPool) {
	this.objectPool = (OxyObjectPoolImpl)objectPool;
	this.objectPool.setEntityManager(this);
}

public ClassDescription getObjectDescription(Class<?> entityClass) {
	ClassDescription cd = schemaDescription.getClassDescription(entityClass.getName());
	if(cd== null) {
		throw new IllegalArgumentException("No object description for entityClass " + entityClass);
	}
	return cd;
}

}
