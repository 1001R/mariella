package at.hts.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.hts.persistence.query.Literal;

@SuppressWarnings("unchecked")
public class EnumConverter<T extends Enum> implements Converter<T> {
	private Class<T> enumClass;

public EnumConverter(Class<T> enumClass) {
	super();
	this.enumClass = enumClass;
}
	
public T getObject(ResultSet rs, int index) throws SQLException {
	String value = rs.getString(index);
	if(value == null) {
		return null;
	} else {
		return Enum.valueOf(enumClass, value);
	}
}

public void setObject(PreparedStatement ps, int index, int type, T value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setObject(index, value.toString());
	}
}

public Literal<T> createLiteral(Object value) {
	throw new UnsupportedOperationException();
}

public String toString(T value) {
	return value == null ? "null" : value.toString();
}

}
