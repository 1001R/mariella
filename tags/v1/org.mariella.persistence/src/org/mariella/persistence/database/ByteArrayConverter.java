package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.ByteArrayLiteral;
import org.mariella.persistence.query.Literal;

public class ByteArrayConverter implements Converter<byte[]> {
	public static final ByteArrayConverter Singleton = new ByteArrayConverter();
	
@Override
public Literal<byte[]> createDummy() {
	byte bytes[] = new byte[] { 0 };
	return createLiteral(bytes);
}

@Override
public Literal<byte[]> createLiteral(Object value) {
	return new ByteArrayLiteral(this, (byte[])value);
}

@Override
public byte[] getObject(ResultSet rs, int index) throws SQLException {
	return rs.getBytes(index);
}

@Override
public void setObject(PreparedStatement ps, int index, int type, byte[] value) throws SQLException {
	ps.setBytes(index, value);
}

@Override
public String toString(byte[] value) {
	if(value == null) {
		return null;
	} else {
		StringBuilder sb = new StringBuilder();
		sb.append("'");
		for(byte b : value) {
			sb.append(Integer.toString((int)b, 16));
		}
		sb.append("'");
		return sb.toString();
	}
}

}
