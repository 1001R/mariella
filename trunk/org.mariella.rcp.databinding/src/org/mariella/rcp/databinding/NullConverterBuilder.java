package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class NullConverterBuilder extends ConverterBuilder {

public NullConverterBuilder() {
}

public IConverter buildFromModelConverter(BindingDomain domain) {
	return new Converter(domain.getType(), domain.getType()) {
		public Object convert(Object fromObject) {
			return "";
		}
	};
}

public IConverter buildToModelConverter(BindingDomain domain) {
	throw new UnsupportedOperationException();
}

}