package org.mariella.oxygen.remoting.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;

public abstract class Invokable<T> {
	protected class ResultObjectOutputStream extends ObjectOutputStream {
		public ResultObjectOutputStream(OutputStream out) throws IOException {
			super(out);
			enableReplaceObject(true);
		}

		@Override
		protected Object replaceObject(Object obj) throws IOException {
			if (obj != null && objectPool.getEntityManager().getSchemaDescription().getClassDescription(obj.getClass().getName()) != null) {
				EntityState state = objectPool.getEntityState(obj);
				Object entity = state.getEntity();
				if (!objectPool.contains(entity))
					throw new IllegalStateException();
				ResultEntityPlaceholder id = new ResultEntityPlaceholder();
				id.poolIdentity = state.getPoolIdentity();
				return id;
			}
			return obj;
		}
	}

	
	private OxyObjectPool objectPool;
	private T command;
	private Object result;
	
public Invokable() {
	super();
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

public void setResult(Object result) {
	this.result = result;
}

@SuppressWarnings("unchecked")
public void invoke(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
	command= (T)inputStream.readObject(); 
	invoke();
}

protected abstract void invoke();

public void writeResult(ObjectOutputStream outputStream) throws IOException {
	outputStream.writeObject(objectPool);
	
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ResultObjectOutputStream roos = new ResultObjectOutputStream(bos);
	roos.writeObject(result);

	outputStream.writeObject(bos.toByteArray());
}

}
