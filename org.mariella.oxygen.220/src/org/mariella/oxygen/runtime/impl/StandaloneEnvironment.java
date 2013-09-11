package org.mariella.oxygen.runtime.impl;

import java.util.Map;

import javax.persistence.PersistenceException;

import org.mariella.oxygen.basic_impl.DefaultClassResolver;
import org.mariella.oxygen.runtime.core.OxyConnectionProvider;
import org.mariella.persistence.annotations.processing.ClassLoaderPersistenceUnitParser;
import org.mariella.persistence.annotations.processing.OxyUnitInfoBuilder;
import org.mariella.persistence.annotations.processing.PersistenceUnitParser;
import org.mariella.persistence.mapping.OxyUnitInfo;

public class StandaloneEnvironment extends EnvironmentImpl {
	
public StandaloneEnvironment(String emName) {
	super();
}

public OxyEntityTransactionFactory createEntityTransactionFactory() {
	return new DefaultEntityTransactionFactory();
}

@Override
public void initialize(String emName, Map<?, ?> properties) {
	try {
		this.properties = properties;
		PersistenceUnitParser parser = new ClassLoaderPersistenceUnitParser(getClass().getClassLoader());
		OxyUnitInfoBuilder  builder = new OxyUnitInfoBuilder(parser);
		builder.build();
	
		for (OxyUnitInfo oxyUnitInfo : builder.getOxyUnitInfos()) {
			if (oxyUnitInfo.getPersistenceUnitName().equals(emName)) {
				this.oxyUnitInfo = oxyUnitInfo;
			}
		}
		if (this.oxyUnitInfo == null) {
			throw new IllegalStateException("Could not find any META-INF/persistence.xml having name " + emName);
		}
		
		persistenceClassResolver = new DefaultClassResolver(getClass().getClassLoader());

		createSchemaMapping();
	} catch (Throwable t) {
		throw new PersistenceException(t);
	}
}


public OxyConnectionProvider createConnectionProvider() {
	String connectString = getStringProperty(OxyJ2SEConnectionProvider.CONNECT_PROPERTY_NAME);
	String dbUser = getStringProperty(OxyJ2SEConnectionProvider.DBUSER_PROPERTY_NAME);
	String dbPassword = getStringProperty(OxyJ2SEConnectionProvider.DBPASSWORD_PROPERTY_NAME);
	return new OxyJ2SEConnectionProvider(connectString, dbUser, dbPassword);
}

//private Bundle getPersistenceBundle(Map<?, ?> properties) {
//String persistenceBundleName = (String) properties.get("persistenceBundleName");
//if(persistenceBundleName != null) {
//	String persistenceBundleVersion = (String) properties.get("persistenceBundleVersion");
//	Bundle[] bundles = Platform.getBundles(persistenceBundleName, persistenceBundleVersion);
//	if(bundles != null && bundles.length > 0) {
//		return bundles[0];
//	}
//}
//return null;
//}


}
