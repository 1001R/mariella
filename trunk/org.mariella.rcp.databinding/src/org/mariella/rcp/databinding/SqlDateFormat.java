package org.mariella.rcp.databinding;

import java.sql.Date;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class SqlDateFormat extends SimpleDateFormat {
private static final long serialVersionUID = 1L;

public SqlDateFormat(String pattern) {
	super(pattern);
}

public Date parse(String text, ParsePosition pos) {
	java.util.Date date = super.parse(text, pos);
	if (date == null) return null;
	return new Date(date.getTime());
}

}
