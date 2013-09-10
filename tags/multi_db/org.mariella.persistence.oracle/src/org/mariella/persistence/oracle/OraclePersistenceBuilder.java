package org.mariella.persistence.oracle;

import java.sql.Types;

import org.mariella.persistence.annotations.mapping_builder.DatabaseInfoProvider;
import org.mariella.persistence.annotations.mapping_builder.PersistenceBuilder;
import org.mariella.persistence.annotations.mapping_builder.PersistenceInfo;
import org.mariella.persistence.annotations.mapping_builder.ConverterRegistryImpl.ConverterFactoryImpl;
import org.mariella.persistence.mapping.OxyUnitInfo;

public class OraclePersistenceBuilder extends PersistenceBuilder {

public OraclePersistenceBuilder(OxyUnitInfo unitInfo, DatabaseInfoProvider databaseInfoProvider) {
	super(unitInfo, databaseInfoProvider);
	converterRegistry.registerConverterFactory(Types.CLOB, String.class, new ConverterFactoryImpl(ClobConverter.Singleton));
}

@Override
protected PersistenceInfo createPersistenceInfo() {
	return new PersistenceInfo(new OracleSchema());
}

}
