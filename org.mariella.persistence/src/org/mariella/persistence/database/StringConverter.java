package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.Literal;
import org.mariella.persistence.query.StringLiteral;


public class StringConverter implements Converter<String> {
	public static StringConverter Singleton = new StringConverter();
	
public String getObject(ResultSet rs, int index) throws SQLException {
	return rs.getString(index);
}

public void setObject(PreparedStatement ps, int index, int type, String value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setString(index, value);
	}
}

@Override
public Literal<String> createDummy() {
	return createLiteral("");
}

public Literal<String> createLiteral(Object value) {
	return new StringLiteral(this, (String)value);
}

public String toString(String value) {
	if (value != null) {
		return value == null ? "null" : "'" + value.toString().replaceAll("'", "''") + "'";
		
	} return "null";
}
}
