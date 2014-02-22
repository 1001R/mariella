package org.mariella.persistence.persistor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.database.InsertStatementBuilder;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.ColumnMapping;
import org.mariella.persistence.runtime.ModifiableAccessor;

public class PrimaryInsertStatementBuilder extends InsertStatementBuilder {
	private final ObjectPersistor objectPersistor;
	
public PrimaryInsertStatementBuilder(ObjectPersistor objectPersistor, Row row) {
	super(row);
	this.objectPersistor = objectPersistor;
}

@Override
public void execute(PreparedStatementManager psManager) {
	try {
		ClassMapping classMapping = objectPersistor.getClassMapping();
		if(classMapping.getPrimaryKey().getGeneratedByDatabaseColumnMappings().length > 0) {
			String sql = getInsertString();
			PreparedStatement ps = psManager.prepareSingleStatement(sql, classMapping.getPrimaryKey().getGeneratedByDatabaseColumnNames());
			try {
				setParameters(ps);
				ps.execute();
				ResultSet rs = ps.getGeneratedKeys();
				try {
					rs.next();
					int idx = 0;
					for(ColumnMapping columnMapping : classMapping.getPrimaryKey().getGeneratedByDatabaseColumnMappings()) {
						idx++;
						Object value = columnMapping.getUpdateColumn().getObject(rs, idx);
						ModifiableAccessor.Singleton.setValue(
							objectPersistor.getModificationInfo().getObject(), 
							columnMapping.getPropertyDescription(), 
							value
						);
					}
				} finally {
					rs.close();
				}
			} finally {
				ps.close();
			}
			return;
		}
		super.execute(psManager);
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}
}

}
