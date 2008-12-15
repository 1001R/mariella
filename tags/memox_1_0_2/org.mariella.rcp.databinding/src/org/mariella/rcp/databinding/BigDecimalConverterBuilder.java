package org.mariella.rcp.databinding;

import java.math.BigDecimal;

import org.apache.commons.validator.routines.BigDecimalValidator;

public class BigDecimalConverterBuilder extends CommonsValidatorConverterBuilder {

public BigDecimalConverterBuilder(String pattern) {
	super(BigDecimalValidator.getInstance(), new BigDecimalFormat(pattern), pattern, BigDecimal.class);
}

}
