package org.mariella.persistence.springtest.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.basic_impl.OxyEntityManagerImpl;
import org.mariella.oxygen.basic_impl.OxyObjectPoolImpl;
import org.mariella.oxygen.remoting.common.Invoker;


public class TestCommandInvoker extends Invoker<Command<?>> {

public TestCommandInvoker(ClassResolver classResolver) {
	super(classResolver);
}

public void invoke() throws IOException, ClassNotFoundException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(bos);
	
	super.invoke(oos);
	oos.flush();
	
	InvokableCommand invokableCommand = new InvokableCommand();
	invokableCommand.invoke(new ByteArrayInputStream(bos.toByteArray()));
	
	ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
	invokableCommand.writeResult(bos2);
	
	readResult(new ByteArrayInputStream(bos2.toByteArray()));
}

@Override
protected void mergeIntoMyPool(OxyObjectPool otherPool) throws ClassNotFoundException {
	((OxyObjectPoolImpl)otherPool).setEntityManager((OxyEntityManagerImpl) getObjectPool().getEntityManager());
	if(isSendObjectPool()) {
		getObjectPool().mergeRelated(otherPool);
	} else {
		getObjectPool().merge(otherPool);
	}
}

}
