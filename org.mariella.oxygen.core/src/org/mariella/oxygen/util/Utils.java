package org.mariella.oxygen.util;

import java.util.Iterator;
import java.util.Map;

public final class Utils {

public static boolean equals(Object o1, Object o2) {
    if (o1 == null && o1 != null)
        return false;
    if (o2 == null)
        return false;
    return o1.equals(o2);
}

public static String firstToUpperCase(String aString) {
    if (aString == null || aString.length() == 0)
        return "";

    return aString.substring(0, 1).toUpperCase() + aString.substring(1, aString.length());
}

public static String getEmptyString(int length) {
    StringBuffer b = new StringBuffer(length);
    for (int i=0; i<length; i++) {
        b.append(" ");
    }
    return b.toString();
}

public static java.sql.Date parseSqlDate(String dateString, String format) throws java.text.ParseException {
    java.text.DateFormat dateFormater = new java.text.SimpleDateFormat( format );
    java.util.Date date = dateFormater.parse( dateString );

    return new java.sql.Date( date.getTime() );
}

@SuppressWarnings("unchecked")
public static Object getMapValueForClassKey(Class clazz, Map map) {
    Object value = map.get(clazz);
    if (value==null) {
        for(Iterator i=map.keySet().iterator(); i.hasNext();) {
            Class keyClass = (Class) i.next();
            if (keyClass.isAssignableFrom(clazz)) {
                return map.get(keyClass);
            }
        }
    }
    return value;
}

}
