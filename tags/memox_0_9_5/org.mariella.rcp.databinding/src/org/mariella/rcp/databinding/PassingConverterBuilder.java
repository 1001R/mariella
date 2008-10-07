package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class PassingConverterBuilder extends ConverterBuilder {

public PassingConverterBuilder() {
}

@Override
public IConverter buildFromModelConverter(VBindingDomain domain) {
	return new Converter(domain.getType(), domain.getType()) {
		public Object convert(Object fromObject) {
			return fromObject;
		}
	};
}

@Override
public IConverter buildToModelConverter(VBindingDomain domain) {
	return new Converter(domain.getType(), domain.getType()) {
		public Object convert(Object fromObject) {
			return fromObject;
		}
	};
}

}