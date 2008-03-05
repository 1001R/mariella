package org.mariella.rcp.databinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;

public class BigDecimalFormat extends DecimalFormat {
private static final long serialVersionUID = 1L;

public BigDecimalFormat() {
}

public BigDecimalFormat(String pattern) {
	super(pattern);
}

public BigDecimalFormat(String pattern, DecimalFormatSymbols symbols) {
	super(pattern, symbols);
}

public Number parse(String text, ParsePosition pos) {
	Number number = super.parse(text, pos);
	if (!(number instanceof BigDecimal)) 
		return new BigDecimal(number.doubleValue());
	return number;
}

}
