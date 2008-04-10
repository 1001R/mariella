package org.mariella.rcp.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarUtil {
public static Timestamp getTodayPlus(int negOrPosValue) {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTimeInMillis(System.currentTimeMillis());
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + negOrPosValue);
	return new Timestamp(cal.getTimeInMillis());
}

}
