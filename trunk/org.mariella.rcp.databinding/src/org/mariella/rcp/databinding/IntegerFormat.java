package org.mariella.rcp.databinding;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;

public class IntegerFormat extends DecimalFormat {
private static final long serialVersionUID = 1L;

public IntegerFormat() {
}

public IntegerFormat(String pattern) {
	super(pattern);
}

public IntegerFormat(String pattern, DecimalFormatSymbols symbols) {
	super(pattern, symbols);
}

public Number parse(String text, ParsePosition pos) {
	Number number = super.parse(text, pos);
	if (!(number instanceof Integer)) 
		return new Integer(number.intValue());
	return number;
}

}
