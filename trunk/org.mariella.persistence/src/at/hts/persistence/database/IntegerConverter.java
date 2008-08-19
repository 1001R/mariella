package at.hts.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.hts.persistence.query.IntegerLiteral;
import at.hts.persistence.query.Literal;

public class IntegerConverter implements Converter<Integer> {
	public static IntegerConverter Singleton = new IntegerConverter();
	
public Integer getObject(ResultSet rs, int index) throws SQLException {
	int value = rs.getInt(index);
	return rs.wasNull() ? null : value;
}

public void setObject(PreparedStatement ps, int index, int type, Integer value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setInt(index, ((Integer)value).intValue());
	}
}

public Literal<Integer> createLiteral(Object value) {
	return new IntegerLiteral(this, (Integer)value);
}

public String toString(Integer value) {
	return value == null ? "null" : value.toString();
}
}
