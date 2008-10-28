package org.mariella.persistence.database;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.persistence.query.DateLiteral;
import org.mariella.persistence.query.Literal;


public class DateConverter implements Converter<Date> {
	public static DateConverter Singleton = new DateConverter();
	
public Date getObject(ResultSet rs, int index) throws SQLException {
	return rs.getDate(index);
}

public void setObject(PreparedStatement ps, int index, int type, Date value) throws SQLException {
	ps.setDate(index, value);
}

public Literal<Date> createLiteral(Object value) {
	return new DateLiteral(this, (Date)value);
}

public String toString(Date value) {
	return value == null ? "null" : value.toString();
}

}
