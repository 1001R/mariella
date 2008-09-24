package org.mariella.persistence.annotations.mapping_builder;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMetaDataDatabaseInfoProvider implements DatabaseInfoProvider {
	private DatabaseMetaData databaseMetaData;
	private List<DatabaseTableInfo> tableInfos = new ArrayList<DatabaseTableInfo>();
	
public DatabaseMetaDataDatabaseInfoProvider(DatabaseMetaData databaseMetaData)  {
	super();
	this.databaseMetaData = databaseMetaData;
}
	
public DatabaseTableInfo getTableInfo(String catalog, String schema, String tableName) {
	for(DatabaseTableInfo dti : tableInfos) {
		if(equals(catalog, dti.getCatalog()) && equals(schema, dti.getSchema()) && equals(tableName, dti.getName())) {
			return dti;
		} 
	}
	return loadTableInfo(catalog, schema, tableName);
}

private boolean equals(String s1, String s2) {
	if(s1 == null && s2 == null) {
		return true;
	} else if(s1 == null || s2 == null) {
		return false;
	} else {
		return s1.equalsIgnoreCase(s2);
	}
}

public DatabaseTableInfo loadTableInfo(String catalog, String schema, String tableName) {
	try {
		if(catalog != null && catalog.length() == 0) {
			catalog = null;
		}
		if(schema != null && schema.length() == 0) {
			schema = null;
		}
		ResultSet rs = databaseMetaData.getTables(catalog, schema, tableName, null);
		try {
			if(rs.next()) {
				DatabaseTableInfo tableInfo = new DatabaseTableInfo();
				tableInfo.setCatalog(rs.getString(1));
				tableInfo.setSchema(rs.getString(2));
				tableInfo.setName(rs.getString(3));
				
				loadColumnInfos(tableInfo);
				loadPrimaryKey(tableInfo);

				tableInfos.add(tableInfo);
				return tableInfo;
			} else {
				return null;
			}
		} finally {
			rs.close();
		}
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}

}

private void loadColumnInfos(DatabaseTableInfo tableInfo) throws SQLException {
	ResultSet rs = databaseMetaData.getColumns(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName(), null);
	try {
		while(rs.next()) {
			DatabaseColumnInfo info = new DatabaseColumnInfo();
			info.setName(rs.getString(4));
			info.setType(rs.getInt(5));
			int i;
			i = rs.getInt(7);
			if(!rs.wasNull()) {
				info.setLength(7);
			}
			i = rs.getInt(9);
			if(!rs.wasNull()) {
				info.setScale(i);
			}
			int nullable = rs.getInt(11);
			if(nullable == DatabaseMetaData.attributeNullableUnknown) {
				throw new RuntimeException("Cannot determine nullable for column " + tableInfo.getName() + "." + info.getName() + "!");
			}
			info.setNullable(nullable == DatabaseMetaData.attributeNullable);
			tableInfo.addColumnInfo(info);
		}
	} finally {
		rs.close();
	}
}

private void loadPrimaryKey(DatabaseTableInfo tableInfo) throws SQLException {
	ResultSet rs = databaseMetaData.getPrimaryKeys(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName());
	try {
		while(rs.next()) {
			String columnName = rs.getString(4);
			DatabaseColumnInfo columnInfo = tableInfo.getColumnInfo(columnName);
			if(columnInfo == null) {
				throw new IllegalStateException("Unkown column for primary key");
			}
			tableInfo.getPrimaryKey().add(columnInfo);
		}
	} finally {
		rs.close();
	}
	
	
}

}
