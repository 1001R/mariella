package org.mariella.rcp.databinding;

import java.sql.Date;


public class SqlDateConverterBuilder extends CommonsValidatorConverterBuilder {

public SqlDateConverterBuilder(String pattern) {
	super(null, new SqlDateFormat(pattern), pattern, Date.class);
}

}
