package org.mariella.persistence.annotations.mapping_builder;

import org.mariella.persistence.annotations.mapping_builder.ConverterRegistryImpl.ConverterFactory;
import org.mariella.persistence.database.Converter;
import org.mariella.persistence.schema.ScalarPropertyDescription;


public interface ConverterRegistry {
public Converter<?> getNamedConverter(String converterName);
public Converter<?> getConverterForColumn(ScalarPropertyDescription propertyDescription, DatabaseTableInfo tableInfo, DatabaseColumnInfo columnInfo);
public void registerConverterFactory(int sqlType, Class<?> propertyType, ConverterFactory converterFactory);
public void registerConverter(String converterName, Converter<?> converter);
}
