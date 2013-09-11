package org.mariella.persistence.h2;

import org.mariella.persistence.annotations.mapping_builder.DatabaseInfoProvider;
import org.mariella.persistence.annotations.mapping_builder.PersistenceBuilder;
import org.mariella.persistence.annotations.mapping_builder.PersistenceInfo;
import org.mariella.persistence.mapping.OxyUnitInfo;

public class H2PersistenceBuilder extends PersistenceBuilder {

public H2PersistenceBuilder(OxyUnitInfo unitInfo, DatabaseInfoProvider databaseInfoProvider) {
	super(unitInfo, databaseInfoProvider);
}

@Override
protected PersistenceInfo createPersistenceInfo() {
	return new PersistenceInfo(new H2Schema());
}

}
