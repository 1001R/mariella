package org.mariella.persistence.postgres;

import org.mariella.persistence.annotations.mapping_builder.DatabaseInfoProvider;
import org.mariella.persistence.annotations.mapping_builder.PersistenceBuilder;
import org.mariella.persistence.annotations.mapping_builder.PersistenceInfo;
import org.mariella.persistence.mapping.OxyUnitInfo;

public class PostgresPersistenceBuilder extends PersistenceBuilder {

public PostgresPersistenceBuilder(OxyUnitInfo unitInfo, DatabaseInfoProvider databaseInfoProvider) {
	super(unitInfo, databaseInfoProvider);
}

@Override
protected PersistenceInfo createPersistenceInfo() {
	return new PersistenceInfo(new PostgresSchema());
}

}
