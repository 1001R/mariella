package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class PassingConverterBuilder extends ConverterBuilder {

public PassingConverterBuilder() {
}

public IConverter buildFromModelConverter(BindingDomain domain) {
	return new Converter(domain.getType(), domain.getType()) {
		public Object convert(Object fromObject) {
			return fromObject;
		}
	};
}

public IConverter buildToModelConverter(BindingDomain domain) {
	return new Converter(domain.getType(), domain.getType()) {
		public Object convert(Object fromObject) {
			return fromObject;
		}
	};
}

}
