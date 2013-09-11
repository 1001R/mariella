/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mariella.oxygen.spring;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.mariella.oxygen.runtime.core.OxySchemaMappingProvider;
import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.oxygen.runtime.impl.OxyEntityManagerFactory;
import org.mariella.persistence.mapping.SchemaMapping;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class OxyDataSourceTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(OxyDataSourceTransactionManager.class);

	class TransactionObject {
		OxyDataSourceTransactionManagerManagedTransaction transactionImpl;
		Integer previousIsolationLevel;
		boolean mustRestoreAutoCommit;
		OxyDataSourceConnectionProvider connectionProvider;
		OxyDataSourceEntityManagerConnectionHolder connectionHolder;
		int countNestedTransactions = 0;
	}

	private DataSource dataSource;
	private OxyEntityManagerFactory entityManagerFactory;
	private SchemaMapping schemaMapping;
	private OxySchemaMappingProvider schemaMappingProvider;

	public OxyDataSourceTransactionManager() {
		setNestedTransactionAllowed(true);
	}

	public OxyDataSourceTransactionManager(DataSource dataSource) {
		this();
		setDataSource(dataSource);
		afterPropertiesSet();
	}

	public void setDataSource(DataSource dataSource) {
		if (dataSource instanceof TransactionAwareDataSourceProxy) {
			this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
		}
		else {
			this.dataSource = dataSource;
		}
	}

	/**
	 * Return the JDBC DataSource that this instance manages transactions for.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	public void afterPropertiesSet() {
		if (dataSource == null) {
			throw new IllegalArgumentException("dataSource is required");
		}
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			schemaMapping = schemaMappingProvider.provideSchemaMapping(connection);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
		}
	}


	@SuppressWarnings("unchecked")
	protected Object doGetTransaction() {
		TransactionObject txObject = (TransactionObject)TransactionSynchronizationManager.getResource(TransactionObject.class);
		if (txObject != null)
			return txObject;
		
		OxyDataSourceConnectionProvider connectionProvider = new OxyDataSourceConnectionProvider(dataSource); 
		
		HashMap props = new HashMap();
		props.put("connectionProvider", connectionProvider);
		props.put("entityTransactionFactory", new OxyDataSourceTransactionManagerManagedTransactionFactory());
		props.put("schemaMapping", schemaMapping);
		
		OxyServerEntityManager entityManager = (OxyServerEntityManager)entityManagerFactory.createEntityManager(props);
		List<String> history = (List<String>)entityManager.getObjectPool().getAttribute("history");
		if(history == null) {
			history = new ArrayList<String>();
			entityManager.getObjectPool().setAttribute("history", history);
		}
		history.add("created on server entitymanager at " + new Date().toString());

		txObject = new TransactionObject();
		txObject.transactionImpl = (OxyDataSourceTransactionManagerManagedTransaction) entityManager.getTransaction();;
		txObject.connectionProvider = connectionProvider;
		TransactionSynchronizationManager.bindResource(TransactionObject.class, txObject);
		return txObject;
	}

	protected boolean isExistingTransaction(Object transaction) {
		TransactionObject t = (TransactionObject) transaction;
		return t.transactionImpl.isActive();
	}

	protected void doBegin(Object transaction, TransactionDefinition definition) {
		TransactionObject txObject = (TransactionObject) transaction;

		if (TransactionSynchronizationManager.getResource(getDataSource()) == null) {
			Connection connection = null;
			try {
				OxyEntityManagerInitializer initializer = OxyDataSourceEntityManagerProviderContext.getOxyEntityManagerInitializerForCurrentThread();
				if (initializer != null)
					initializer.initializeEntityManager((OxyServerEntityManager) txObject.transactionImpl.getEntityManager());
				connection = dataSource.getConnection();
				OxyDataSourceEntityManagerConnectionHolder connectionHolder = new OxyDataSourceEntityManagerConnectionHolder(connection, (OxyServerEntityManager)txObject.transactionImpl.getEntityManager());
				txObject.connectionHolder = connectionHolder;
	
				Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(connection, definition);
				txObject.previousIsolationLevel = previousIsolationLevel;
	
				if (connection.getAutoCommit()) {
					txObject.mustRestoreAutoCommit = true;
					connection.setAutoCommit(false);
				}
	
				TransactionSynchronizationManager.bindResource(getDataSource(), connectionHolder);
	
				connectionHolder.entityManager.getTransaction().begin();
			} catch (SQLException ex) {
				if (connection != null)
					DataSourceUtils.releaseConnection(connection, this.dataSource);
				throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", ex);
			}
		} else {
			
		}
		txObject.countNestedTransactions++;
	}

	protected Object doSuspend(Object transaction) {
		TransactionObject txObject = (TransactionObject) transaction;
		txObject.connectionHolder = null;
		OxyDataSourceEntityManagerConnectionHolder conHolder = (OxyDataSourceEntityManagerConnectionHolder)TransactionSynchronizationManager.unbindResource(this.dataSource);
		return conHolder;
	}

	protected void doResume(Object transaction, Object suspendedResources) {
		OxyDataSourceEntityManagerConnectionHolder conHolder = (OxyDataSourceEntityManagerConnectionHolder) suspendedResources;
		TransactionSynchronizationManager.bindResource(this.dataSource, conHolder);
	}

	protected void doCommit(DefaultTransactionStatus status) {
		TransactionObject txObject = (TransactionObject) status.getTransaction();
		Connection con = txObject.connectionHolder.getConnection();
		if (status.isDebug()) {
			logger.debug("Committing JDBC transaction on Connection [" + con + "]");
		}
		if (!status.isReadOnly())
			txObject.transactionImpl.commit();
		try {
			con.commit();
		}
		catch (SQLException ex) {
			throw new TransactionSystemException("Could not commit JDBC transaction", ex);
		}
	}

	protected void doRollback(DefaultTransactionStatus status) {
		TransactionObject txObject = (TransactionObject) status.getTransaction();
		Connection con = txObject.connectionHolder.getConnection();
		if (status.isDebug()) {
			logger.debug("Rolling back JDBC transaction on Connection [" + con + "]");
		}
		try {
			con.rollback();
		}
		catch (SQLException ex) {
			throw new TransactionSystemException("Could not roll back JDBC transaction", ex);
		}
	}

	protected void doSetRollbackOnly(DefaultTransactionStatus status) {
		TransactionObject txObject = (TransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Setting JDBC transaction [" + txObject.connectionHolder.getConnection() +
					"] rollback-only");
		}
		txObject.connectionHolder.setRollbackOnly();
	}

	protected void doCleanupAfterCompletion(Object transaction) {
		TransactionObject txObject = (TransactionObject) transaction;
		txObject.countNestedTransactions--;

		if (txObject.countNestedTransactions == 0) {
			TransactionSynchronizationManager.unbindResource(TransactionObject.class);
			TransactionSynchronizationManager.unbindResource(this.dataSource);
	
			// Reset connection.
			Connection con = txObject.connectionHolder.getConnection();
			try {
				if (txObject.mustRestoreAutoCommit) {
					con.setAutoCommit(true);
				}
				DataSourceUtils.resetConnectionAfterTransaction(con, txObject.previousIsolationLevel);
			}
			catch (Throwable ex) {
				logger.debug("Could not reset JDBC Connection after transaction", ex);
			}
			DataSourceUtils.releaseConnection(con, this.dataSource);
	
			txObject.connectionHolder.clear();
		}
	}

	public void setEntityManagerFactory(OxyEntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public OxySchemaMappingProvider getSchemaMappingProvider() {
		return schemaMappingProvider;
	}

	public void setSchemaMappingProvider(
			OxySchemaMappingProvider schemaMappingProvider) {
		this.schemaMappingProvider = schemaMappingProvider;
	}


}
