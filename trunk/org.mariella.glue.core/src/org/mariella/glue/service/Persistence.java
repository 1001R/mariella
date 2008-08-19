package org.mariella.glue.service;


import at.hts.persistence.mapping.SchemaMapping;

public interface Persistence {
public Context createContext();
public SchemaMapping getSchemaMapping();
}
