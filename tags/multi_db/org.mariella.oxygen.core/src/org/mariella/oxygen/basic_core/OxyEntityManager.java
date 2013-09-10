package org.mariella.oxygen.basic_core;

import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.SchemaDescription;

public interface OxyEntityManager {

public OxyObjectPool getObjectPool();
public void setObjectPool(OxyObjectPool objectPool);
public RuntimeException createRuntimeException(Throwable t);
public SchemaDescription getSchemaDescription();
public ClassDescription getClassDescription(Class<?> entityClass);

/**
 * @param objectDescription
 * @return the entity class for the object description.  *
 */
public Class<?> getClass(ClassDescription classDescription);

public ClassResolver getEntityClassResolver();

}
