package org.mariella.oxygen.runtime.impl;


import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.mariella.oxygen.basic_impl.BundleClassResolver;
import org.mariella.oxygen.runtime.core.OxyConnectionProvider;
import org.mariella.persistence.annotations.processing.OxyUnitInfoBuilder;
import org.mariella.persistence.mapping.OxyUnitInfo;
import org.mariella.persistence.mapping.SchemaMapping;
import org.osgi.framework.Bundle;

/**
 * @author aim
 */
public class OxyEntityManagerFactory implements EntityManagerFactory {
	private static final Logger logger = Logger.getLogger(OxyEntityManagerFactory.class.getName());

	private boolean isOpen = true;
	private String emName;
	// TODO mart: added OxyUnitInfo as property for debugging
	private OxyUnitInfo oxyUnitInfo;
	private SchemaMapping schemaMapping;
	private BundleClassResolver persistenceClassResolver;

public OxyEntityManagerFactory(String emName, Map<?, ?> properties) {
	super();
	try {
		this.emName = emName;
		persistenceClassResolver = new BundleClassResolver(
				(String) properties.get("persistenceBundleName"),
				(String) properties.get("persistenceBundleVersion"));
		oxyUnitInfo = fetchOxyUnitInfo(persistenceClassResolver.resolveBundle());
	} catch (Throwable t) {
		throw new PersistenceException(t);
	}
}

//private Bundle getPersistenceBundle(Map<?, ?> properties) {
//	String persistenceBundleName = (String) properties.get("persistenceBundleName");
//	if(persistenceBundleName != null) {
//		String persistenceBundleVersion = (String) properties.get("persistenceBundleVersion");
//		Bundle[] bundles = Platform.getBundles(persistenceBundleName, persistenceBundleVersion);
//		if(bundles != null && bundles.length > 0) {
//			return bundles[0];
//		}
//	}
//	return null;
//}

public void close() {
	if (logger.isLoggable(Level.FINE))
		logger.fine("close");
	isOpen = false;
}

public EntityManager createEntityManager() {
	checkOpen();
	return createEntityManager(Collections.EMPTY_MAP);
}

public EntityManager createEntityManager(Map properties) {
	checkOpen();

	try {
		OxyConnectionProvider connectionProvider = null;
		connectionProvider = (OxyConnectionProvider)properties.get("connectionProvider");
		if (connectionProvider == null) {
			String jdbcDriver = oxyUnitInfo.getProperties().getProperty("org.mariella.oxygen.jdbcdriver");
			if (jdbcDriver != null) {
				DriverManager.registerDriver((Driver)Class.forName(jdbcDriver).newInstance());
			}
			String connectString = (String)oxyUnitInfo.getProperties().get(OxyJ2SEConnectionProvider.CONNECT_PROPERTY_NAME);
			String dbUser = (String)oxyUnitInfo.getProperties().get(OxyJ2SEConnectionProvider.DBUSER_PROPERTY_NAME);
			String dbPassword = (String)oxyUnitInfo.getProperties().get(OxyJ2SEConnectionProvider.DBPASSWORD_PROPERTY_NAME);
			connectionProvider = new OxyJ2SEConnectionProvider(connectString, dbUser, dbPassword);
		}

		OxyEntityTransactionFactory transactionFactory = null;
		if (properties != null)
			transactionFactory = (OxyEntityTransactionFactory)properties.get("entityTransactionFactory");
		if (transactionFactory == null)
			transactionFactory = new DefaultEntityTransactionFactory();

		SchemaMapping schemaMapping = (SchemaMapping)properties.get("schemaMapping");

		return new OxyServerEntityManagerImpl(connectionProvider, schemaMapping,
				transactionFactory, persistenceClassResolver);

	} catch(RuntimeException e) {
		throw e;
	} catch (Exception e) {
		throw new PersistenceException(e);
	}
}

private OxyUnitInfo fetchOxyUnitInfo(Bundle persistenceBundle) throws Exception {
	OxyUnitInfoBuilder  builder = new OxyUnitInfoBuilder();
	if(persistenceBundle != null) {
		builder.setBundle(persistenceBundle);
	} else {
		builder.setClassLoader(getClass().getClassLoader());
	}
	builder.build();

	for (OxyUnitInfo oxyUnitInfo : builder.getOxyUnitInfos()) {
		if (oxyUnitInfo.getPersistenceUnitName().equals(emName))
			return oxyUnitInfo;
	}
	throw new IllegalStateException("Could not find any META-INF/persistence.xml having name " + emName);
}

public boolean isOpen() {
	return isOpen;
}

private void checkOpen() {
	if(!isOpen) {
		throw new IllegalStateException("EntityManagerFactory is not open!");
	}
}

public OxyUnitInfo getOxyUnitInfo() {
	return oxyUnitInfo;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

}
