package org.mariella.persistence.springtest.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.oxygen.spring.OxyEntityManagerProvider;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class Service {
	private TransactionTemplate transactionTemplate;
	private OxyEntityManagerProvider entityManagerProvider;
	
public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
	this.transactionTemplate = transactionTemplate;
}

public void setEntityManagerProvider(OxyEntityManagerProvider entityManagerProvider) {
	this.entityManagerProvider = entityManagerProvider;
}
	
public void test() {
	transactionTemplate.execute(
			new TransactionCallback<Object>() {
				@Override
				public Object doInTransaction(TransactionStatus arg0) {
					OxyServerEntityManager em = entityManagerProvider.getEntityManager();
					//new SimpleTest(em).referentialTest();
					return null;
				}
			}
		);

	transactionTemplate.execute(
		new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus arg0) {
				OxyServerEntityManager em = entityManagerProvider.getEntityManager();
				new SimpleTest(em).step1();
				return null;
			}
		}
	);

	transactionTemplate.execute(
		new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus arg0) {
				OxyServerEntityManager em = entityManagerProvider.getEntityManager();
				new SimpleTest(em).step2();
				return null;
			}
		}
	);
	
	transactionTemplate.execute(
		new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus arg0) {
				OxyServerEntityManager em = entityManagerProvider.getEntityManager();
				new SimpleTest(em).step3();
				return null;
			}
		}
	);
}


}
