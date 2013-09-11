package org.mariella.cat.core.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class IntegerFormat extends Format {
	private static final long serialVersionUID = 1L;
	
	private boolean allowNegative;
	
	public IntegerFormat() {
		this(true);
	}
	
	public IntegerFormat(boolean allowNegative) {
		this.allowNegative = allowNegative;
		
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		String s = obj.toString();
		toAppendTo.append(s);
		pos.setEndIndex(pos.getBeginIndex()+s.length());
		return toAppendTo;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		int startIndex = pos.getIndex();
		int endIndex = startIndex;
		if (endIndex < source.length()) {
			if (allowNegative && source.charAt(endIndex) == '-') {
				endIndex++;
			}
			while (endIndex < source.length() && Character.isDigit(source.charAt(endIndex))) {
				endIndex++;
			}
		}
		if (endIndex == startIndex) {
			pos.setErrorIndex(startIndex);
		} else {
			try {
				int nr = Integer.parseInt(source.substring(startIndex, endIndex));
				pos.setIndex(endIndex);
				return nr;
			} catch (NumberFormatException ex) {
				pos.setErrorIndex(startIndex);
			}
		}
		return null;
	}

}
