package org.mariella.rcp.util;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class CalendarUtil {
public static Timestamp getTodayPlus(int negOrPosValue) {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTimeInMillis(System.currentTimeMillis());
	cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
	cal.set(GregorianCalendar.MINUTE, 0);
	cal.set(GregorianCalendar.SECOND, 0);
	cal.set(GregorianCalendar.MILLISECOND, 0);
	cal.set(GregorianCalendar.DAY_OF_YEAR, cal.get(GregorianCalendar.DAY_OF_YEAR) + negOrPosValue);
	return new Timestamp(cal.getTimeInMillis());
}

}
