package org.mariella.persistence.springtest.service;

import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.basic_impl.OxyEntityManagerImpl;
import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.oxygen.spring.OxyEntityManagerProvider;
import org.mariella.persistence.schema.SchemaDescription;
import org.mariella.persistence.springtest.model.Person;
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
	
public Person execute(final LoadPersonCommand command) {
	return transactionTemplate.execute(
		new TransactionCallback<Person>() {
			@Override
			public Person doInTransaction(TransactionStatus arg0) {
				OxyServerEntityManager em = entityManagerProvider.getEntityManager();
				return new RemotingTest(em).execute(command);
			}
		}
	);
	
}

public void execute(final LoadExtendedPersonCommand command) {
	transactionTemplate.execute(
		new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus arg0) {
				OxyServerEntityManager em = entityManagerProvider.getEntityManager();
				new RemotingTest(em).execute(command);
				return null;
			}
		}
	);
	
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

public SchemaDescription getSchemaDescription() {
	return transactionTemplate.execute(new TransactionCallback<SchemaDescription>() {
		@Override
		public SchemaDescription doInTransaction(TransactionStatus status) {
			return entityManagerProvider.getEntityManager().getSchemaDescription();
		}
	});
}


public OxyEntityManagerImpl getEntityManager() {
	return transactionTemplate.execute(new TransactionCallback<OxyEntityManagerImpl>() {
		@Override
		public OxyEntityManagerImpl doInTransaction(TransactionStatus status) {
			return (OxyEntityManagerImpl) entityManagerProvider.getEntityManager();
		}
	});
}

public TransactionTemplate getTransactionTemplate() {
	return transactionTemplate;
}

}
