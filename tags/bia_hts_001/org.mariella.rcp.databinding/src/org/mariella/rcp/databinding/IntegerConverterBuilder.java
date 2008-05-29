package org.mariella.rcp.databinding;

import org.apache.commons.validator.routines.IntegerValidator;

public class IntegerConverterBuilder extends CommonsValidatorConverterBuilder {

public IntegerConverterBuilder(String pattern) {
	super(IntegerValidator.getInstance(), new IntegerFormat(pattern), pattern, Integer.class);
}

}
