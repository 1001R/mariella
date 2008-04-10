package org.mariella.rcp.databinding;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class SqlDateToDateTimeConverterBuilder extends ConverterBuilder {

public SqlDateToDateTimeConverterBuilder() {
}

@Override
public IConverter buildFromModelConverter(VBindingDomain domain) {
	return new Converter(domain.getType(), Calendar.class) {
		public Object convert(Object fromObject) {
			GregorianCalendar cal = new GregorianCalendar();
			if (fromObject == null) return cal;
			
			Date date = (Date)fromObject;
			cal.setTime(date);
			return cal;
		}
	};
}

@Override
public IConverter buildToModelConverter(VBindingDomain domain) {
	return new Converter(Calendar.class, domain.getType()) {
		public Object convert(Object fromObject) {
			GregorianCalendar cal = (GregorianCalendar)fromObject;
			return new Date(cal.getTime().getTime());
		}
	};
}

}
