package org.mariella.oxygen.runtime.impl;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.PersistenceException;

import org.mariella.oxygen.basic_impl.EntityListener;

public class ImmediateEntityListener extends EntityListener {

@Override
protected void perform(Method[] methods, Object entity) {
	if(methods != null) {
		for(Method method : methods) {
			try {
				method.invoke(entity, new Object[] {});
			} catch(InvocationTargetException e) {
				throw new PersistenceException(e);
			} catch(IllegalAccessException e) {
				throw new PersistenceException(e);
			}
		}
	}
}

}
