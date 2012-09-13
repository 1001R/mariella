package org.mariella.rcp.databinding;

import org.apache.commons.validator.routines.LongValidator;

public class LongConverterBuilder extends CommonsValidatorConverterBuilder {

public LongConverterBuilder(String pattern) {
	super(LongValidator.getInstance(), new IntegerFormat(pattern), pattern, Long.class);
}

public LongConverterBuilder(String pattern, boolean allowNullValues) {
	super(LongValidator.getInstance(), new IntegerFormat(pattern), pattern, Long.class, allowNullValues);
}

}
