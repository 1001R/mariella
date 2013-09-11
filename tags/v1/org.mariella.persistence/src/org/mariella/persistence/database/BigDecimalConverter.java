package org.mariella.persistence.database;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.BigDecimalLiteral;
import org.mariella.persistence.query.Literal;


public class BigDecimalConverter implements Converter<BigDecimal> {
	public static BigDecimalConverter Singleton = new BigDecimalConverter();
	
public BigDecimal getObject(ResultSet rs, int index) throws SQLException {
	return rs.getBigDecimal(index);
}

public void setObject(PreparedStatement ps, int index, int type, BigDecimal value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setBigDecimal(index, value);
	}
}

public Literal<BigDecimal> createLiteral(Object value) {
	return new BigDecimalLiteral(this, (BigDecimal)value);
}

@Override
public Literal<BigDecimal> createDummy() {
	return createLiteral(new BigDecimal("0"));
}

public String toString(BigDecimal value) {
	return value == null ? "null" : "'" + value.toString() + "'";
}
}
