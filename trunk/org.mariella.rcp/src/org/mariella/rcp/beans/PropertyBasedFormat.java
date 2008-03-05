package org.mariella.rcp.beans;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class PropertyBasedFormat extends Format {
private static final long serialVersionUID = 1L;

PropertyAccessor propertyAccessor;

public PropertyBasedFormat(PropertyAccessor propertyAccessor) {
	this.propertyAccessor = propertyAccessor;
}

public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
	toAppendTo.append(propertyAccessor.readValue(obj));
	return toAppendTo;
}

public Object parseObject(String source, ParsePosition pos) {
	throw new UnsupportedOperationException();
}

}
