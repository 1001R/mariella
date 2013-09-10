package org.mariella.oxygen.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.mariella.oxygen.runtime.core.OxyConnectionProvider;
import org.mariella.oxygen.runtime.impl.EnvironmentImpl;
import org.mariella.oxygen.runtime.impl.OxyDataSourceConnectionProvider;
import org.mariella.oxygen.runtime.impl.OxyEntityTransactionFactory;
import org.mariella.persistence.annotations.processing.OxyUnitInfoBuilder;
import org.mariella.persistence.mapping.OxyUnitInfo;
import org.mariella.persistence.runtime.PersistenceException;
import org.osgi.framework.Bundle;

public class EclipseEnvironment extends EnvironmentImpl {
	private OxyConnectionProvider connectionProvider;
	private OxyEntityTransactionFactory entityTransactionFactory;

public EclipseEnvironment() {
	super();
	properties = new HashMap<String, Object>();
}

public void initialize(String emName) {
	try {
		OsgiPersistenceUnitParser parser = new OsgiPersistenceUnitParser();
		OxyUnitInfoBuilder builder = new OxyUnitInfoBuilder(parser);
		builder.build();

		for (OxyUnitInfo oxyUnitInfo : builder.getOxyUnitInfos()) {
			if (oxyUnitInfo.getPersistenceUnitName().equals(emName))
				this.oxyUnitInfo = oxyUnitInfo;
		}
		if (this.oxyUnitInfo == null) {
			throw new IllegalStateException("Could not find any META-INF/persistence.xml having name " + emName);
		}
		
		List<Bundle> bundles = parser.getBundles();
		List<String> bundleIds = new ArrayList<String>(bundles.size());
		for (Bundle bundle : bundles) {
			bundleIds.add(bundle.getSymbolicName());
		}
		persistenceClassResolver = new BundleClassResolver(bundleIds);
		createSchemaMapping();
	} catch (Throwable t) {
		throw new PersistenceException(t);
	}
}

public OxyConnectionProvider createConnectionProvider() {
	return connectionProvider;
}

public void setConnectionProvider(OxyConnectionProvider connectionProvider) {
	this.connectionProvider = connectionProvider;
}

public OxyEntityTransactionFactory createEntityTransactionFactory() {
	return entityTransactionFactory;
}

public void setEntityTransactionFactory(OxyEntityTransactionFactory entityTransactionFactory) {
	this.entityTransactionFactory = entityTransactionFactory;
}

public void setDataSource(DataSource dataSource) {
	setConnectionProvider(new OxyDataSourceConnectionProvider(dataSource));
}

}
