package org.mariella.cat.core.format;

import java.text.ParsePosition;
import java.text.StringCharacterIterator;

public class NonBlankStringFormat extends StringFormat {
	private static final long serialVersionUID = 1L;

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		int startIndex = pos.getIndex();
		String s = (String) super.parseObject(source, pos);
		StringCharacterIterator it = new StringCharacterIterator(s);
		for (char ch = it.first(); ch != StringCharacterIterator.DONE; ch = it.next()) {
			if (!Character.isWhitespace(ch)) {
				return s;
			}
		}
		pos.setIndex(startIndex);
		pos.setErrorIndex(pos.getIndex());
		return null;
	}

}
