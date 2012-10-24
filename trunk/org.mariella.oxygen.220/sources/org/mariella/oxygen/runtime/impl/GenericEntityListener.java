package org.mariella.oxygen.runtime.impl;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.PersistenceException;

import org.mariella.oxygen.basic_impl.EntityListener;

public class GenericEntityListener extends EntityListener {
	private Object entityListener;

public GenericEntityListener(Object entityListener) {
	super();
	this.entityListener = entityListener;
}
	
@Override
protected void perform(Method[] methods, Object entity) {
	if(methods != null) {
		for(Method method : methods) {
			try {
				method.invoke(entityListener, new Object[] { entity });
			} catch(IllegalAccessException e) {
				throw new PersistenceException(e);
			} catch(InvocationTargetException e) {
				throw new PersistenceException(e);
			}
		}
	}
}

}
