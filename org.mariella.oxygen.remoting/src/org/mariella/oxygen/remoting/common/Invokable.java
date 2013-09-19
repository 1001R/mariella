package org.mariella.oxygen.remoting.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
			if (obj != null && objectPool != null && objectPool.getEntityManager().getSchemaDescription().getClassDescription(obj.getClass().getName()) != null) {
				EntityState state = objectPool.getEntityState(obj);
				Object entity = state.getEntity();
				if (!objectPool.contains(entity))
					throw new IllegalStateException();
				ResultEntityPlaceholder id = new ResultEntityPlaceholder();
				id.poolIdentity = state.getPoolIdentity();
				id.persistentIdentity = state.getPersistentIdentity();
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

public Object getResult() {
	return result;
}

public void setResult(Object result) {
	this.result = result;
}

public T getCommand() {
	return command;
}

protected void invoke(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
	objectPool = (OxyObjectPool)ois.readObject();
	setObjectPool(objectPool);
	command = (T)ois.readObject();
}

public void writeResult(OutputStream outputStream) throws IOException {
	boolean inputStreamResult = result instanceof InputStreamAndLength;
	outputStream.write(inputStreamResult ? ResultFlags.STREAM : ResultFlags.NONE);
	
	ObjectOutputStream oos = new ObjectOutputStream(outputStream);
	oos.writeObject(objectPool);
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ResultObjectOutputStream roos = new ResultObjectOutputStream(bos);
	
	roos.writeObject(result);

	oos.writeObject(bos.toByteArray());

	if (result instanceof InputStreamAndLength) {
		InputStream resultStream = ((InputStreamAndLength)result).getInputStream();
		
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = resultStream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
		} finally {
			resultStream.close();
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}	
}

}
