package org.mariella.persistence.annotations.mapping_builder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.mariella.persistence.database.BigDecimalConverter;
import org.mariella.persistence.database.BooleanAsNumberConverter;
import org.mariella.persistence.database.ByteArrayConverter;
import org.mariella.persistence.database.CharArrayConverter;
import org.mariella.persistence.database.Converter;
import org.mariella.persistence.database.DateConverter;
import org.mariella.persistence.database.DoubleConverter;
import org.mariella.persistence.database.EnumConverter;
import org.mariella.persistence.database.IntegerConverter;
import org.mariella.persistence.database.LongConverter;
import org.mariella.persistence.database.NStringConverter;
import org.mariella.persistence.database.StringConverter;
import org.mariella.persistence.database.TimestampConverter;
import org.mariella.persistence.schema.ScalarPropertyDescription;


public class ConverterRegistryImpl implements ConverterRegistry {
	public static interface ConverterFactory {
		public Converter <?> createConverter(ScalarPropertyDescription propertyDescription, DatabaseTableInfo tableInfo, DatabaseColumnInfo columnInfo);
	}

	public static class ConverterFactoryImpl implements ConverterFactory {
		private final Converter<?> converter;

		public ConverterFactoryImpl(Converter<?> converter) {
			super();
			this.converter = converter;
		}

		@Override
		public Converter<?> createConverter(ScalarPropertyDescription propertyDescription, DatabaseTableInfo tableInfo, DatabaseColumnInfo columnInfo) {
			return converter;
		}
	}

	private final Map<String, Converter<?>> namedConverters = new HashMap<String, Converter<?>>();
	private final Map<Integer, Map<Class<?>, ConverterFactory>> converterFactories = new HashMap<Integer, Map<Class<?>, ConverterFactory>>();

public ConverterRegistryImpl() {
	super();
	registerConverterFactory(Types.VARCHAR, String.class, new ConverterFactoryImpl(StringConverter.Singleton));
	registerConverterFactory(Types.CHAR, String.class, new ConverterFactoryImpl(StringConverter.Singleton));
	registerConverterFactory(Types.VARBINARY, String.class, new ConverterFactoryImpl(StringConverter.Singleton));
	registerConverterFactory(Types.LONGVARCHAR, String.class, new ConverterFactoryImpl(StringConverter.Singleton));
	registerConverterFactory(Types.OTHER, String.class, new ConverterFactoryImpl(NStringConverter.Singleton));
	registerNumericConverters(Types.INTEGER);
	registerNumericConverters(Types.BIGINT);
	registerNumericConverters(Types.DECIMAL);
	registerNumericConverters(Types.NUMERIC);

	registerConverterFactory(Types.FLOAT, BigDecimal.class, new ConverterFactoryImpl(BigDecimalConverter.Singleton));

	registerConverterFactory(Types.FLOAT, Double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.DOUBLE, Double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.NUMERIC, Double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.DECIMAL, Double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.INTEGER, Double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));

	registerConverterFactory(Types.FLOAT, double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.DOUBLE, double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.NUMERIC, double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.DECIMAL, double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));
	registerConverterFactory(Types.INTEGER, double.class, new ConverterFactoryImpl(DoubleConverter.Singleton));

	registerConverterFactory(Types.DATE, Timestamp.class, new ConverterFactoryImpl(TimestampConverter.Singleton));
	registerConverterFactory(Types.TIMESTAMP, Timestamp.class, new ConverterFactoryImpl(TimestampConverter.Singleton));
	registerConverterFactory(Types.DATE, Date.class, new ConverterFactoryImpl(DateConverter.Singleton));
	registerConverterFactory(Types.DATE, java.util.Date.class, new ConverterFactoryImpl(DateConverter.Singleton));
	registerConverterFactory(Types.TIMESTAMP, java.util.Date.class, new ConverterFactoryImpl(DateConverter.Singleton));
	registerConverterFactory(Types.TIMESTAMP, Date.class, new ConverterFactoryImpl(DateConverter.Singleton));

	registerConverterFactory(Types.BLOB, byte[].class, new ConverterFactoryImpl(ByteArrayConverter.Singleton));
	registerConverterFactory(Types.BINARY, byte[].class, new ConverterFactoryImpl(ByteArrayConverter.Singleton));
	registerConverterFactory(Types.CLOB, char[].class, new ConverterFactoryImpl(CharArrayConverter.Singleton));
}

protected void registerNumericConverters(int type) {
	registerConverterFactory(type, Integer.class, new ConverterFactoryImpl(IntegerConverter.Singleton));
	registerConverterFactory(type, int.class, new ConverterFactoryImpl(IntegerConverter.Singleton));
	registerConverterFactory(type, Long.class, new ConverterFactoryImpl(LongConverter.Singleton));
	registerConverterFactory(type, long.class, new ConverterFactoryImpl(LongConverter.Singleton));
	registerConverterFactory(type, BigDecimal.class, new ConverterFactoryImpl(BigDecimalConverter.Singleton));
	registerConverterFactory(type, boolean.class, new ConverterFactoryImpl(BooleanAsNumberConverter.Singleton));
	registerConverterFactory(type, Boolean.class, new ConverterFactoryImpl(BooleanAsNumberConverter.Singleton));
}


@Override
public void registerConverterFactory(int sqlType, Class<?> propertyType, ConverterFactory converterFactory) {
	Map<Class<?>, ConverterFactory> map = converterFactories.get(sqlType);
	if(map == null) {
		map = new HashMap<Class<?>, ConverterFactory>();
		converterFactories.put(sqlType, map);
	}
	map.put(propertyType, converterFactory);
}

@Override
public void registerConverter(String converterName, Converter<?> converter) {
	namedConverters.put(converterName, converter);
}

@Override
public Converter<?> getNamedConverter(String converterName) {
	Converter<?> converter = namedConverters.get(converterName);
	if(converter == null) {
		throw new IllegalArgumentException("Unknown converter named '" + converterName + "'!");
	} else {
		return converter;
	}
}

@Override
@SuppressWarnings("unchecked")
public Converter<?> getConverterForColumn(ScalarPropertyDescription propertyDescription, DatabaseTableInfo tableInfo, DatabaseColumnInfo columnInfo) {
	ConverterFactory factory = null;
	Map<Class<?>, ConverterFactory> map = converterFactories.get(columnInfo.getType());
	if(map != null) {
		factory = map.get(propertyDescription.getPropertyDescriptor().getPropertyType());
		if(factory != null) {
			Converter<?> converter = factory.createConverter(propertyDescription, tableInfo, columnInfo);
			if(converter != null) {
				return converter;
			}
		}
	}

	if((columnInfo.getType() == Types.VARCHAR || columnInfo.getType() == Types.CHAR) && propertyDescription.getPropertyDescriptor().getPropertyType().isEnum()) {
		return new EnumConverter(propertyDescription.getPropertyDescriptor().getPropertyType());
	}

	throw new IllegalArgumentException("Cannot create converter for property " + propertyDescription.getClassDescription().getClassName() + "." + propertyDescription.getPropertyDescriptor().getName());
}



}
