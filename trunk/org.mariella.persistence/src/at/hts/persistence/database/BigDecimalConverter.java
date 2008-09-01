package at.hts.persistence.database;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.hts.persistence.query.BigDecimalLiteral;
import at.hts.persistence.query.Literal;

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

public String toString(BigDecimal value) {
	return value == null ? "null" : "'" + value.toString() + "'";
}
}
