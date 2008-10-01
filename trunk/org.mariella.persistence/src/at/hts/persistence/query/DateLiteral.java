package at.hts.persistence.query;

import java.sql.Date;

import at.hts.persistence.database.Converter;
import at.hts.persistence.database.DateConverter;

public class DateLiteral extends Literal<Date> {
	
public DateLiteral(Date value) {
	super(DateConverter.Singleton, value);
}

public DateLiteral(Converter<Date> converter, Date value) {
	super(converter, value);
}

public void printSql(StringBuilder b) {
	throw new UnsupportedOperationException();
}

}
