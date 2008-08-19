package at.hts.persistence.query;

import java.sql.Timestamp;

import at.hts.persistence.database.Converter;
import at.hts.persistence.database.TimestampConverter;

public class TimestampLiteral extends Literal<Timestamp> {
	
public TimestampLiteral(Timestamp value) {
	super(TimestampConverter.Singleton, value);
}

public TimestampLiteral(Converter<Timestamp> converter, Timestamp value) {
	super(converter, value);
}

public void printSql(StringBuilder b) {
	throw new UnsupportedOperationException();
}

}
