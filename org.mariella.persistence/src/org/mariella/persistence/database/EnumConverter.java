package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.Literal;


@SuppressWarnings("unchecked")
public class EnumConverter<T extends Enum> implements Converter<T> {
	private Class<T> enumClass;

public EnumConverter(Class<T> enumClass) {
	super();
	this.enumClass = enumClass;
}

protected T getEnumValue(String string) {
	return (T)Enum.valueOf(enumClass, string);
}

protected String getString(T value) {
	return value == null ? null : value.toString();
}

public T getObject(ResultSet rs, int index) throws SQLException {
	String value = rs.getString(index);
	if(value == null) {
		return null;
	} else {
		return getEnumValue(value);
	}
}

public void setObject(PreparedStatement ps, int index, int type, T value) throws SQLException {
	String string = getString(value);
	if(string == null) {
		ps.setNull(index, type);
	} else {
		ps.setObject(index, string);
	}
}

public Literal<T> createLiteral(Object value) {
	throw new UnsupportedOperationException();
}

@Override
public Literal<T> createDummy() {
	return new Literal<T>(this, null) {
		@Override
		public void printSql(StringBuilder b) {
			b.append("''");
		}
	};
}

public String toString(T value) {
	String string = getString(value);
	return string == null ? "null" : string;
}

}
