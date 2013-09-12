package org.mariella.persistence.springtest.service;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.remoting.common.Invokable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class InvokableCommand extends Invokable<Command<?>> {

@Override
protected void invoke() {
	getCommand().setObjectPool(getObjectPool());
	SpringTestService.service.getTransactionTemplate().execute(new TransactionCallback<Object>() {
		@Override
		public Object doInTransaction(TransactionStatus status) {
			try {
				SpringTestService.service.getEntityManager().setObjectPool(getObjectPool());
				
				Object result = null;
				if (getCommand() instanceof LoadPersonCommand)
					result = SpringTestService.service.execute((LoadPersonCommand)getCommand());
				else if (getCommand() instanceof LoadExtendedPersonCommand)
					SpringTestService.service.execute((LoadExtendedPersonCommand)getCommand());
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

@Override
public void invoke(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
	OxyObjectPool objectPool = (OxyObjectPool)inputStream.readObject();
	setObjectPool(objectPool);
	super.invoke(inputStream);
}

}
