package org.mariella.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.DoubleLiteral;
import org.mariella.persistence.query.Literal;

public class DoubleConverter implements Converter<Double> {

	public static DoubleConverter Singleton = new DoubleConverter();
	
@Override
public Literal<Double> createLiteral(Object value) {
	return new DoubleLiteral(this, (Double)value);
}

@Override
public Literal<Double> createDummy() {
	return createLiteral(0.0d);
}

@Override
public Double getObject(ResultSet rs, int index) throws SQLException {
	double value = rs.getDouble(index);
	return rs.wasNull() ? null : value;
}

@Override
public void setObject(PreparedStatement ps, int index, int type,
		Double value) throws SQLException {
	if(value == null) {
		ps.setNull(index, type);
	} else {
		ps.setDouble(index, value.doubleValue());
	}

}

@Override
public String toString(Double value) {
	return value == null ? "null" : value.toString();
}

}
