package org.mariella.oxygen.runtime.impl;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

/**
 * @author aim
 */
public class OxyPersistenceProvider implements PersistenceProvider {

public OxyPersistenceProvider() {
	super();
}

public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
	throw new UnsupportedOperationException("Container environments are not supported!");
}

public EntityManagerFactory createEntityManagerFactory(String emName, Map properties) {
	Environment environment = (Environment)properties.get("org.mariella.oxygen.environment");
	if(environment == null) {
		environment = new StandaloneEnvironment(emName);
	}
	environment.initialize(emName, properties);

	return new OxyEntityManagerFactory(emName, properties, environment);
}


}
