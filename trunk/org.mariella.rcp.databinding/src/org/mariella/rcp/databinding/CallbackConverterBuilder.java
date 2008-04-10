package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class CallbackConverterBuilder extends ConverterBuilder {

ConversionCallback conversionCallback;

public CallbackConverterBuilder(ConversionCallback conversionCallback) {
	this.conversionCallback = conversionCallback;
}

@Override
public IConverter buildFromModelConverter(final VBindingDomain domain) {
	return new Converter(domain.getType(), String.class) {
		public Object convert(Object fromObject) {
			return conversionCallback.getTextForObject(domain.getDomainContext(), fromObject);
		}
	};
}

@Override
public IConverter buildToModelConverter(final VBindingDomain domain) {
	return new Converter(String.class, domain.getType()) {
		public Object convert(Object fromObject) {
			return conversionCallback.getObjectForText(domain.getDomainContext(), (String)fromObject);
		}
	};
}

}
