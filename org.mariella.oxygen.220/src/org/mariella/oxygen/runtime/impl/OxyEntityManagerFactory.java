package org.mariella.oxygen.runtime.impl;


import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

/**
 * @author aim
 */
public class OxyEntityManagerFactory implements EntityManagerFactory {
	private static final Logger logger = Logger.getLogger(OxyEntityManagerFactory.class.getName());

	private boolean isOpen = true;
	private Environment environment;
	
public OxyEntityManagerFactory(String emName, Map<?, ?> properties, Environment environment) {
	super();
	this.environment = environment;
}

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
		return new OxyServerEntityManagerImpl(
			environment.createConnectionProvider(), 
			environment.getSchemaMapping(), 
			environment.createEntityTransactionFactory(), 
			environment.getPersistenceClassResolver()
		);
	} catch(RuntimeException e) {
		throw e;
	} catch (Exception e) {
		throw new PersistenceException(e);
	}
}

public boolean isOpen() {
	return isOpen;
}

private void checkOpen() {
	if(!isOpen) {
		throw new IllegalStateException("EntityManagerFactory is not open!");
	}
}


}
