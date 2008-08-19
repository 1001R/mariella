package at.hts.persistence.query;

import at.hts.persistence.database.Converter;

public class BooleanLiteral extends Literal<Boolean> {
	
public BooleanLiteral(Converter<Boolean> converter, Boolean value) {
	super(converter, value);
}

}
