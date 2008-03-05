package org.mariella.rcp.databinding;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampFormat extends SimpleDateFormat {
private static final long serialVersionUID = 1L;

public TimestampFormat(String pattern) {
	super(pattern);
}

public Date parse(String text, ParsePosition pos) {
	Date date = super.parse(text, pos);
	if (date == null) return null;
	return new Timestamp(date.getTime());
}

}
