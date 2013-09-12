package org.mariella.oxygen.remoting.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.remoting.common.ResultEntityPlaceholder;

// client
public class ResultObjectInputStream extends ObjectInputStream {
	final OxyObjectPool mergedObjectPool;
	final ClassResolver classResolver;
	
	public ResultObjectInputStream(InputStream is, OxyObjectPool mergedObjectPool, ClassResolver classResolver) throws IOException {
		super(is);
		this.mergedObjectPool = mergedObjectPool;
		this.classResolver = classResolver;
		enableResolveObject(true);
	}
	@Override
	protected Object resolveObject(Object obj) throws IOException {
		if (obj instanceof ResultEntityPlaceholder) {
			ResultEntityPlaceholder id = (ResultEntityPlaceholder)obj;
			Object entity = mergedObjectPool.getEntityStateForPoolId(id.poolIdentity).getEntity();
			return entity;
		}
		return super.resolveObject(obj);
	}
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
		return classResolver.resolveClass(desc.getName());
	}

}
