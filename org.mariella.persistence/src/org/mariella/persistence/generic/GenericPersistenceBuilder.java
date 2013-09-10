package org.mariella.persistence.generic;

import org.mariella.persistence.annotations.mapping_builder.DatabaseInfoProvider;
import org.mariella.persistence.annotations.mapping_builder.PersistenceBuilder;
import org.mariella.persistence.annotations.mapping_builder.PersistenceInfo;
import org.mariella.persistence.mapping.OxyUnitInfo;

public class GenericPersistenceBuilder extends PersistenceBuilder {

public GenericPersistenceBuilder(OxyUnitInfo unitInfo, DatabaseInfoProvider databaseInfoProvider) {
	super(unitInfo, databaseInfoProvider);
}

@Override
protected PersistenceInfo createPersistenceInfo() {
	return new PersistenceInfo(new GenericSchema());
}

}
