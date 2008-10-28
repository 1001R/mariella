package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.mariella.persistence.query.Literal;
import org.mariella.persistence.query.TimestampLiteral;


public class TimestampConverter implements Converter<Timestamp> {
	public static TimestampConverter Singleton = new TimestampConverter();
	
public Timestamp getObject(ResultSet rs, int index) throws SQLException {
	return rs.getTimestamp(index);
}

public void setObject(PreparedStatement ps, int index, int type, Timestamp value) throws SQLException {
	ps.setTimestamp(index, value);
}

public Literal<Timestamp> createLiteral(Object value) {
	return new TimestampLiteral(this, (Timestamp)value);
}

public String toString(Timestamp value) {
	return value == null ? "null" : value.toString();
}

}
