package org.mariella.oxygen.runtime.core;


import java.sql.Connection;

import org.mariella.oxygen.basic_core.ObjectPool;
import org.mariella.persistence.mapping.SchemaMapping;

public interface OxyContext {
public ObjectPool getObjectPool();
public Connection getConnection();
public SchemaMapping getSchemaMapping();


}
