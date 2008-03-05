package org.mariella.rcp.databinding;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;

public class SqlDateToDateTimeConverterBuilder extends ConverterBuilder {

public SqlDateToDateTimeConverterBuilder() {
}

public IConverter buildFromModelConverter(BindingDomain domain) {
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

public IConverter buildToModelConverter(BindingDomain domain) {
	return new Converter(Calendar.class, domain.getType()) {
		public Object convert(Object fromObject) {
			GregorianCalendar cal = (GregorianCalendar)fromObject;
			return new Date(cal.getTime().getTime());
		}
	};
}

}
