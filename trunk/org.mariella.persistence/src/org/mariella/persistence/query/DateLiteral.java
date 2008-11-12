package org.mariella.persistence.query;

import java.util.Date;

import org.mariella.persistence.database.Converter;
import org.mariella.persistence.database.DateConverter;


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
