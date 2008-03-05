package org.mariella.rcp.databinding;

import java.text.Format;
import java.text.ParseException;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class FormatConverterBuilder extends ConverterBuilder {

Format format;

public FormatConverterBuilder(Format format) {
	this.format = format;
}

public IConverter buildFromModelConverter(BindingDomain domain) {
	return new Converter(domain.getType(), String.class) {
		public Object convert(Object fromObject) {
			return format.format(fromObject);
		}
	};
}

public IConverter buildToModelConverter(BindingDomain domain) {
	return new Converter(String.class, domain.getType()) {
		public Object convert(Object fromObject) {
			try {
				return format.parseObject((String)fromObject);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	};
}

}
