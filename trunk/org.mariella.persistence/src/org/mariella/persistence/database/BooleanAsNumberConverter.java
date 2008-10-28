package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.BooleanLiteral;
import org.mariella.persistence.query.Literal;


public class BooleanAsNumberConverter implements Converter<Boolean> {
	public static BooleanAsNumberConverter Singleton = new BooleanAsNumberConverter();
	
public Boolean getObject(ResultSet rs, int index) throws SQLException {
	int value = rs.getInt(index);
	return rs.wasNull() ? null : value != 0;
}

public void setObject(PreparedStatement ps, int index, int type, Boolean value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setInt(index, value ? 1 : 0);
	}
}

public Literal<Boolean> createLiteral(Object value) {
	return new BooleanLiteral(this, (Boolean)value);
}

public String toString(Boolean value) {
	return value == null ? "null" : (value ? "1" : "0");
}
}
