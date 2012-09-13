package org.mariella.persistence.annotations.mapping_builder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface DatabaseInfoProvider {
	
public DatabaseTableInfo getTableInfo(String catalog, String schema, String tableName);
public void load(ObjectInputStream is) throws IOException, ClassNotFoundException;
public void store(ObjectOutputStream os) throws IOException;
}
