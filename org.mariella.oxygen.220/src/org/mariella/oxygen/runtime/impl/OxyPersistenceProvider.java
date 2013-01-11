package org.mariella.oxygen.runtime.impl;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

/**
 * @author aim
 */
public class OxyPersistenceProvider implements PersistenceProvider {

public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
	throw new UnsupportedOperationException("Container environments are not supported!");
}

public EntityManagerFactory createEntityManagerFactory(String emName, Map properties) {
	return new OxyEntityManagerFactory(emName, properties);
}


}
