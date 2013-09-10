package org.mariella.persistence.mapping;

import java.sql.SQLException;

import org.mariella.persistence.persistor.DatabaseAccess;

public class AutoGenerator extends ColumnValueGenerator {
	
@Override
public boolean isGeneratedByDatabase() {
	return true;
}

@Override
public Object generate(DatabaseAccess dba) throws SQLException {
	throw new UnsupportedOperationException();
}

}
