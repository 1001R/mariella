package at.hts.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.hts.persistence.query.Literal;
import at.hts.persistence.query.LongLiteral;

public class LongConverter implements Converter<Long> {
	public static LongConverter Singleton = new LongConverter();
	
public Long getObject(ResultSet rs, int index) throws SQLException {
	long value = rs.getLong(index);
	return rs.wasNull() ? null : value;
}

public void setObject(PreparedStatement ps, int index, int type, Long value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setLong(index, value.longValue());
	}
}

public Literal<Long> createLiteral(Object value) {
	return new LongLiteral(this, (Long)value);
}

public String toString(Long value) {
	return value == null ? "null" : value.toString();
}
}
