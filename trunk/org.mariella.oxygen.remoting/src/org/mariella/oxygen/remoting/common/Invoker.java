package org.mariella.oxygen.remoting.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.basic_impl.OxyEntityManagerImpl;
import org.mariella.oxygen.basic_impl.OxyObjectPoolImpl;


public abstract class Invoker<T> {
	public class ResultObjectInputStream extends ObjectInputStream {
		public ResultObjectInputStream(InputStream is) throws IOException {
			super(is);
			enableResolveObject(true);
		}
		@Override
		protected Object resolveObject(Object obj) throws IOException {
			if (obj instanceof ResultEntityPlaceholder) {
				ResultEntityPlaceholder id = (ResultEntityPlaceholder)obj;
				Object entity = objectPool.getEntityStateForPoolId(id.poolIdentity).getEntity();
				return entity;
			}
			return super.resolveObject(obj);
		}
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
			return classResolver.resolveClass(desc.getName());
		}

	}
	
	private OxyObjectPool objectPool;
	private T command;
	private Object result;
	
	private ClassResolver classResolver;
	
public Invoker(ClassResolver classResolver) {
	this.classResolver = classResolver;
}

public OxyObjectPool getObjectPool() {
	return objectPool;
}

public void setObjectPool(OxyObjectPool objectPool) {
	this.objectPool = objectPool;
}

public T getCommand() {
	return command;
}

public void setCommand(T command) {
	this.command = command;
}

public Object getResult() {
	return result;
}

public ClassResolver getClassResolver() {
	return classResolver;
}

public void invoke(ObjectOutputStream outputStream) throws IOException {
	outputStream.writeObject(command);
}

public void readResult(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
	OxyObjectPool remotePool = (OxyObjectPool)inputStream.readObject();
	byte[] serializedResult = (byte[])inputStream.readObject();
	
	if(remotePool != null) {
		mergeIntoMyPool(remotePool);
	}

	InputStream is = new ByteArrayInputStream(serializedResult);
	ResultObjectInputStream ris = new ResultObjectInputStream(is);
	result = ris.readObject();
}

private void mergeIntoMyPool(OxyObjectPool otherPool) throws ClassNotFoundException {
	((OxyObjectPoolImpl)otherPool).setEntityManager((OxyEntityManagerImpl) objectPool.getEntityManager());
	objectPool.mergeRelated(otherPool);
}

}
