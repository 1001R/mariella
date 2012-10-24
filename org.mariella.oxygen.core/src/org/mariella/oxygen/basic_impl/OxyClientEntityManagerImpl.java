package org.mariella.oxygen.basic_impl;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.persistence.schema.SchemaDescription;

public class OxyClientEntityManagerImpl extends OxyEntityManagerImpl {

public OxyClientEntityManagerImpl(SchemaDescription schemaDescription, ClassResolver entityClassResolver) {
	super(entityClassResolver, schemaDescription);
}

public EntityListenerRegistry getEntityListenerRegistry() {
	throw new UnsupportedOperationException();
}

public RuntimeException createRuntimeException(Throwable t) {
	return t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
}

}
