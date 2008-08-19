package at.hts.persistence.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Column {
	private String name;
	private int type;
	private Converter<?> converter;
	private boolean nullable = true;
	
public Column(String name, int type, boolean nullable, Converter<?> converter) {
	super();
	this.name = name;
	this.type = type;
	this.converter = converter;
	this.nullable = nullable;
}
	
public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public int getType() {
	return type;
}

public void setType(int type) {
	this.type = type;
}

public void setConverter(Converter<?> converter) {
	this.converter = converter;
}

public Converter<?> getConverter() {
	return converter;
}

@SuppressWarnings("unchecked")
public void setObject(PreparedStatement ps, int index, Object value) throws SQLException {
	((Converter)converter).setObject(ps, index, type, value);
}

@SuppressWarnings("unchecked")
public Object getObject(ResultSet rs, int index) throws SQLException {
	return ((Converter)converter).getObject(rs, index);
}

public boolean isNullable() {
	return nullable;
}

public void setNullable(boolean nullable) {
	this.nullable = nullable;
}

@Override
public String toString() {
	return name;
}

}
