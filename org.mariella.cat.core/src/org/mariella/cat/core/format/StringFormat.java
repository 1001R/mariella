package org.mariella.cat.core.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class StringFormat extends Format {
	private static final long serialVersionUID = 1L;

	public StringFormat() {
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
		String s = source.substring(pos.getIndex(), source.length());
		pos.setIndex(source.length());
		return s;
	}

}
