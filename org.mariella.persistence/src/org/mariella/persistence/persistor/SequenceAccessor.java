package org.mariella.persistence.persistor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.database.ConnectionCallback;
import org.mariella.persistence.database.Sequence;
import org.mariella.persistence.runtime.MariellaPersistence;

public class SequenceAccessor {
	private final Sequence sequence;

	private Long nextId = null;
	private int increment = 0;

public SequenceAccessor(Sequence sequence) {
	super();
	this.sequence = sequence;
}

public long nextValue(final DatabaseAccess dba) {
	if(nextId == null || increment == sequence.getAllocationSize()) {
		try {
			dba.doInConnection(
				new ConnectionCallback() {
					@Override
					public Object doInConnection(Connection connection) throws SQLException {
						String sql = "SELECT " + sequence.getName() + ".NEXTVAL FROM DUAL";
						MariellaPersistence.logger.info(sql);
						PreparedStatement ps = connection.prepareStatement(sql);
						try {
							ResultSet rs = ps.executeQuery();
							try {
								rs.next();
								nextId = rs.getLong(1);
							} finally {
								rs.close();
							}
						} finally {
							ps.close();
						}
						return null;
					}
				}
			);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
		increment = 0;
	}
	long result = nextId.longValue() + increment;
	increment++;
	return result;

}

}
