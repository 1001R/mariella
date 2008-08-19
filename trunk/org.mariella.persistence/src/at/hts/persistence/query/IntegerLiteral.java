package at.hts.persistence.query;

import at.hts.persistence.database.Converter;
import at.hts.persistence.database.IntegerConverter;

public class IntegerLiteral extends Literal<Integer> {
	
public IntegerLiteral(int value) {
	super(IntegerConverter.Singleton, value);
}

public IntegerLiteral(Converter<Integer> converter, int value) {
	super(converter, value);
}

}
