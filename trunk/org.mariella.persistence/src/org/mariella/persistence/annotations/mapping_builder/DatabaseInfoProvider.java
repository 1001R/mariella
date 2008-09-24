package org.mariella.persistence.annotations.mapping_builder;

public interface DatabaseInfoProvider {
	
public DatabaseTableInfo getTableInfo(String catalog, String schema, String tableName);

}
