package org.mariella.rcp.databinding;

import java.text.Format;
import java.text.ParseException;

import org.apache.commons.validator.routines.AbstractFormatValidator;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public abstract class CommonsValidatorConverterBuilder extends ConverterBuilder {

AbstractFormatValidator cValidator;
String pattern;
Format format;
Class modelType;
boolean allowNullValues;

public CommonsValidatorConverterBuilder(AbstractFormatValidator cValidator, Format format, String pattern, Class modelType) {
	this.cValidator = cValidator;
	this.format = format;
	this.modelType = modelType;
}

public CommonsValidatorConverterBuilder(AbstractFormatValidator cValidator, Format format, String pattern, Class modelType, boolean allowNullValues) {
	this.cValidator = cValidator;
	this.format = format;
	this.modelType = modelType;
	this.allowNullValues = allowNullValues;
}

@Override
public IConverter buildFromModelConverter(VBindingDomain domain) {
	return new Converter(modelType, String.class) {
		public Object convert(Object fromObject) {
			try {
				return format.format(fromObject);
			} catch (Exception e) {
				return "";
			}
		}
	};
}

@Override
public IConverter buildToModelConverter(VBindingDomain domain) {
	return new Converter(String.class, modelType) {
		public Object convert(Object fromObject) {
			if ("".equals(fromObject))
				return null;
			if (cValidator != null && !cValidator.isValid((String)fromObject)) {
				if (allowNullValues)
					return null;
				throw new RuntimeException("Not a valid input: " + fromObject);
			}
			try {
				return format.parseObject((String)fromObject);
			} catch (ParseException e) {
				if (allowNullValues)
					return null;
				throw new RuntimeException("Not a valid input: " + fromObject);
			}
		}
	};
}

}
