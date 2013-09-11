package org.mariella.cat.core.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class OptionalFormat extends Format {
	private static final long serialVersionUID = 1L;

	private Format format;
	private String nullValue;
	
	public OptionalFormat(Format format) {
		this(format, "");
	}
	
	public OptionalFormat(Format format, String nullValue) {
		this.format = format;
		this.nullValue = nullValue;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		if (obj == null) {
			toAppendTo.append(nullValue);
			pos.setEndIndex(pos.getBeginIndex()+nullValue.length());
			return toAppendTo;
		} else {
			return format.format(obj, toAppendTo, pos);
		}
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		if (nullValue.isEmpty() && source.isEmpty()) {
			return null;
		} else if (!nullValue.isEmpty() && source.regionMatches(pos.getIndex(), nullValue, 0, nullValue.length())) {
			pos.setIndex(pos.getIndex()+nullValue.length());
			return null;
		} else {
			return format.parseObject(source, pos);
		}
	}

	
	
}
