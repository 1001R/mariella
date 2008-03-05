package org.mariella.rcp.databinding;

import java.sql.Timestamp;


public class TimestampConverterBuilder extends CommonsValidatorConverterBuilder {

public TimestampConverterBuilder(String pattern) {
	super(null, new TimestampFormat(pattern), pattern, Timestamp.class);
}

}