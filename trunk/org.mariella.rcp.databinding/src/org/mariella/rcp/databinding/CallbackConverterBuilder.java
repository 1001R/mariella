package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class CallbackConverterBuilder extends ConverterBuilder {

ConversionCallback conversionCallback;

public CallbackConverterBuilder(ConversionCallback conversionCallback) {
	this.conversionCallback = conversionCallback;
}

public IConverter buildFromModelConverter(final BindingDomain domain) {
	return new Converter(domain.getType(), String.class) {
		public Object convert(Object fromObject) {
			return conversionCallback.getTextForObject(domain.getDomainContext(), fromObject);
		}
	};
}

public IConverter buildToModelConverter(final BindingDomain domain) {
	return new Converter(String.class, domain.getType()) {
		public Object convert(Object fromObject) {
			return conversionCallback.getObjectForText(domain.getDomainContext(), (String)fromObject);
		}
	};
}

}
