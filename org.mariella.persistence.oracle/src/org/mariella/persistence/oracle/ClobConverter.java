package org.mariella.persistence.oracle;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.database.Converter;
import org.mariella.persistence.query.Literal;
import org.mariella.persistence.query.StringLiteral;

public class ClobConverter implements Converter<String> {
	public static ClobConverter Singleton = new ClobConverter();
	
public String getObject(ResultSet rs, int index) throws SQLException {
	Clob clob = rs.getClob(index);
	if(clob == null) {
		return null;
	} else {
		BufferedReader reader = new BufferedReader(clob.getCharacterStream());
		try {
			try {
				char[] buf = new char[1024];
				int len = 0;
				StringBuilder sb = new StringBuilder();
				while ( (len = reader.read(buf))> -1) {
					sb.append(new String(buf, 0, len));
				}
				return sb.toString();
			} finally {
				reader.close();
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}	
}

public void setObject(PreparedStatement ps, int index, int type, String value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		Clob clob = oracle.sql.CLOB.createTemporary(ps.getConnection(), true, oracle.sql.CLOB.DURATION_SESSION);

		clob.setString(1, value);
		ps.setClob(index, clob);
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
