package at.hts.persistence.query;

import java.math.BigDecimal;

import at.hts.persistence.database.BigDecimalConverter;
import at.hts.persistence.database.Converter;

public class BigDecimalLiteral extends Literal<BigDecimal> {
	
public BigDecimalLiteral(BigDecimal bd) {
	super(BigDecimalConverter.Singleton, bd);
}

public BigDecimalLiteral(Converter<BigDecimal> converter, BigDecimal bd) {
	super(converter, bd);
}

}
