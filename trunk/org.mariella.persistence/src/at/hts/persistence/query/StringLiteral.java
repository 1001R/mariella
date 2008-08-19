package at.hts.persistence.query;

import at.hts.persistence.database.Converter;
import at.hts.persistence.database.StringConverter;

public class StringLiteral extends Literal<String> {
	
public StringLiteral(String string) {
	super(StringConverter.Singleton, string);
}

public StringLiteral(Converter<String> converter, String string) {
	super(converter, string);
}

}
