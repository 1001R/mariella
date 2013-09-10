package org.mariella.oxygen.spring;


import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.mariella.oxygen.osgi.EclipseEnvironment;
import org.mariella.oxygen.runtime.impl.OxyEntityManagerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class OxyDataSourceEntityManagerFactoryBean implements FactoryBean, InitializingBean, DisposableBean {
	private static final Logger logger = Logger.getLogger(OxyDataSourceEntityManagerFactoryBean.class.getName());

	private OxyEntityManagerFactory factory;
	private String persistenceUnitName;
	private String persistenceBundleName;
	private String persistenceBundleVersion;
	private DataSource dataSource;
	private EclipseEnvironment environment;

public OxyDataSourceEntityManagerFactoryBean() {
	super();
}

public Object getObject() throws Exception {
	return factory;
}

public Class<?> getObjectType() {
	return EntityManagerFactory.class;
}

public boolean isSingleton() {
	return true;
}

public EclipseEnvironment getEnvironment() {
	return environment;
}

public void setEnvironment(EclipseEnvironment environment) {
	this.environment = environment;
}

public void afterPropertiesSet() throws Exception {
	if (persistenceUnitName == null)
		throw new IllegalStateException("Property 'persistenceUnitName' must be specified for OxyEntityManagerFactoryBean");

	HashMap<String, Object> properties = new HashMap<String, Object>();
	properties.put("dataSource", dataSource);
	properties.put("persistenceBundleName", persistenceBundleName);
	properties.put("persistenceBundleVersion", persistenceBundleVersion);
	properties.put("org.mariella.oxygen.environment", environment);
	
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	ClassLoader clTarget = OxyEntityManagerFactory.class.getClassLoader();
	try {
		Thread.currentThread().setContextClassLoader(clTarget);
		factory = (OxyEntityManagerFactory)Persistence.createEntityManagerFactory(persistenceUnitName, properties);
	} finally {
		Thread.currentThread().setContextClassLoader(cl);
	}

	logger.fine("Created EntityManagerFactory");
}

public void destroy() throws Exception {
	factory.close();
	logger.fine("Closed EntityManagerFactory");
}

public void setProperties(Properties properties) {}

public void setPersistenceUnitName(String persistenceUnitName) {
	this.persistenceUnitName = persistenceUnitName;
}

public DataSource getDataSource() {
	return dataSource;
}

public void setDataSource(DataSource dataSource) {
	this.dataSource = dataSource;
}

public void setPersistenceBundleName(String persistenceBundleName) {
	this.persistenceBundleName = persistenceBundleName;
}

public void setPersistenceBundleVersion(String persistenceBundleVersion) {
	this.persistenceBundleVersion = persistenceBundleVersion;
}

}
