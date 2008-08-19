package at.hts.persistence.query;

import at.hts.persistence.database.Converter;
import at.hts.persistence.database.LongConverter;

public class LongLiteral extends Literal<Long> {
	
public LongLiteral(Long value) {
	super(LongConverter.Singleton, value);
}

public LongLiteral(Converter<Long> converter, Long value) {
	super(converter, value);
}

}
