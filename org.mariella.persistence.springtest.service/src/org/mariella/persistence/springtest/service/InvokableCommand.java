package org.mariella.persistence.springtest.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.remoting.common.Invokable;
import org.mariella.oxygen.remoting.common.RemotingClassResolver;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class InvokableCommand extends Invokable<Command<?>> {

public void invoke(final InputStream inputStream) throws IOException, ClassNotFoundException {
	final RemotingClassResolver resolver = new RemotingClassResolver();

	ObjectInputStream ois = new ObjectInputStream(inputStream) {
		protected java.lang.Class<?> resolveClass(java.io.ObjectStreamClass desc) throws IOException ,ClassNotFoundException {
			return resolver.resolveClass(desc.getName());	
		}
	};

	super.invoke(ois);
	getCommand().setObjectPool(getObjectPool());
	
	SpringTestService.service.getTransactionTemplate().execute(new TransactionCallback<Object>() {
		@Override
		public Object doInTransaction(TransactionStatus status) {
			try {
				if (getObjectPool() != null)
					SpringTestService.service.getEntityManager().setObjectPool(getObjectPool());
				
				Object result = null;
				if (getCommand() instanceof LoadPersonCommand)
					result = SpringTestService.service.execute((LoadPersonCommand)getCommand());
				else if (getCommand() instanceof LoadExtendedPersonCommand)
					SpringTestService.service.execute((LoadExtendedPersonCommand)getCommand());
				else if (getCommand() instanceof SetupDBCommand)
					SpringTestService.service.setupDB();
				else if (getCommand() instanceof CreateTestDataCommand)
					SpringTestService.service.execute((CreateTestDataCommand)getCommand());
				else if (getCommand() instanceof LoadSchemaDescriptionCommand)
					result = SpringTestService.service.getSchemaDescription();
				else if (getCommand() instanceof LoadSomeContentCommand)
					result = SpringTestService.service.loadSomeContent();
				else if (getCommand() instanceof PostSomeContentCommand)
					result = SpringTestService.service.postSomeContent(inputStream);
				else
					throw new IllegalArgumentException();
				setResult(result);
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	});
}

}
