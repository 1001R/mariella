package org.mariella.persistence.springtest.service;

import java.sql.Connection;
import java.sql.DriverManager;

import org.h2.Driver;
import org.mariella.oxygen.spring.OxyEntityManagerProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class SpringTestService implements BundleActivator {

	private static SpringTestService instance;
	public static final String PLUGIN_ID = SpringTestService.class.getPackage().getName();
	
	private static BundleContext context;
	private ClassPathXmlApplicationContext applicationContext;
	
	public static Service service;

public static BundleContext getContext() {
	return context;
}

public static SpringTestService getInstance() {
	return instance;
}

public void start(BundleContext bundleContext) throws Exception {
	if (System.getProperty("REMOTING_TEST_CLIENT_ENV") == null) {
		DriverManager.registerDriver(new Driver());
		Connection connection = DriverManager.getConnection("jdbc:h2:~/test");
		new FileExecutor(getClass().getResourceAsStream("/h2/drop.sql"), connection, false).execute();
		new FileExecutor(getClass().getResourceAsStream("/h2/create.sql"), connection, true).execute();
		connection.close();
		
		SpringTestService.context = bundleContext;
		SpringTestService.instance = this;
		applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml") {
			@Override
			public ClassLoader getClassLoader() {
				return SpringTestService.class.getClassLoader();
			}
		};
		
		service = (Service)applicationContext.getBean("service");
	}
}

public void stop(BundleContext bundleContext) throws Exception {
	if(applicationContext != null) {
		applicationContext.close();
		applicationContext = null;
	}
	SpringTestService.instance = null;
	SpringTestService.context = null;
}

public JdbcTemplate getJdbcTemplate() {
	return applicationContext.getBean("jdbcTemplate", JdbcTemplate.class);
}

public TransactionTemplate getTransactionTemplate() {
	return applicationContext.getBean("transactionTemplate", TransactionTemplate.class);
}

public OxyEntityManagerProvider getEntityManagerProvider() {
	return applicationContext.getBean("entityManagerProvider", OxyEntityManagerProvider.class);
}	

}
