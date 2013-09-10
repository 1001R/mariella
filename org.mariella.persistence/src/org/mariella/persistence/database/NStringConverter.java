package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.Literal;
import org.mariella.persistence.query.StringLiteral;


public class NStringConverter implements Converter<String> {
	public static NStringConverter Singleton = new NStringConverter();

@Override
public String getObject(ResultSet rs, int index) throws SQLException {
	return rs.getNString(index);
}

@Override
public void setObject(PreparedStatement ps, int index, int type, String value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setNString(index, value);
	}
}

@Override
public Literal<String> createDummy() {
	return createLiteral("");
}

@Override
public Literal<String> createLiteral(Object value) {
	return new StringLiteral(this, (String)value);
}

@Override
public String toString(String value) {
	if (value != null) {
		return value == null ? "null" : "'" + value.toString().replaceAll("'", "''") + "'";

	} return "null";
}
}
