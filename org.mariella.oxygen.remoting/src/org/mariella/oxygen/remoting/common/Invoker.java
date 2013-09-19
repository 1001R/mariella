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
				if(sendObjectPool) {
					return objectPool.getEntityStateForPoolId(id.poolIdentity).getEntity();
				} else {
					Object entity = objectPool.getEntityForPersistentId(id.persistentIdentity);
					if(entity == null) {
						entity = obj;
						objectPool.getModificationTracker().addExistingParticipant(entity);
					}
					return entity;
				}
			}
			return super.resolveObject(obj);
		}
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
			return classResolver.resolveClass(desc.getName());
		}

	}

	private boolean sendObjectPool = true;
	private OxyObjectPool objectPool;
	private InputStreamAndLength postedContent = null;
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

public boolean isSendObjectPool() {
	return sendObjectPool;
}

public void setSendObjectPool(boolean sendObjectPool) {
	this.sendObjectPool = sendObjectPool;
}

public Object getResult() {
	return result;
}

public ClassResolver getClassResolver() {
	return classResolver;
}

public void invoke(ObjectOutputStream outputStream) throws IOException {
	if(sendObjectPool) {
		outputStream.writeObject(getObjectPool());
	}
	outputStream.writeObject(getCommand());
}

public void readResult(InputStream inputStream) throws IOException, ClassNotFoundException {
	int resultFlags = inputStream.read();
	@SuppressWarnings("unused")
	boolean inputStreamResult = (resultFlags & ResultFlags.STREAM) != 0;
	
	ObjectInputStream ois = new ObjectInputStream(inputStream) {
		protected java.lang.Class<?> resolveClass(java.io.ObjectStreamClass desc) throws IOException ,ClassNotFoundException {
			return getClassResolver().resolveClass(desc.getName());	
		}
	};

	OxyObjectPool remotePool = (OxyObjectPool)ois.readObject();
	if(remotePool != null) {
		mergeIntoMyPool(remotePool);
	}
	
	byte[] serializedResult = (byte[])ois.readObject();
	InputStream is = new ByteArrayInputStream(serializedResult);
	ResultObjectInputStream ris = new ResultObjectInputStream(is);
	result = ris.readObject();

	if (result instanceof InputStreamAndLength)
		((InputStreamAndLength)result).setInputStream(inputStream);
}

protected void mergeIntoMyPool(OxyObjectPool otherPool) throws ClassNotFoundException {
	((OxyObjectPoolImpl)otherPool).setEntityManager((OxyEntityManagerImpl) objectPool.getEntityManager());
	objectPool.mergeRelated(otherPool);
}

public InputStreamAndLength getPostedContent() {
	return postedContent;
}

public void setPostedContent(InputStreamAndLength postedContent) {
	this.postedContent = postedContent;
}

public boolean isExpectsReturnedContent() {
	return false;
}

public void setResult(Object result) {
	this.result = result;
}

}
