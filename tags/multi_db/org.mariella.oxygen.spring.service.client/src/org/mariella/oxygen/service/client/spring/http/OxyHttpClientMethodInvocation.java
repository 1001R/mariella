package org.mariella.oxygen.service.client.spring.http;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.mariella.oxygen.basic_core.OxyObjectPool;

public class OxyHttpClientMethodInvocation implements MethodInvocation {

MethodInvocation delegate;
OxyObjectPool objectPool;

public OxyHttpClientMethodInvocation(MethodInvocation delegate) {
	this.delegate = delegate;
}

public Method getMethod() {
	return delegate.getMethod();
}

public Object[] getArguments() {
	return delegate.getArguments();
}

public AccessibleObject getStaticPart() {
	return delegate.getStaticPart();
}

public Object getThis() {
	return delegate.getThis();
}

public Object proceed() throws Throwable {
	return delegate.proceed();
}

public OxyObjectPool getObjectPool() {
	return objectPool;
}

public void setObjectPool(OxyObjectPool objectPool) {
	this.objectPool = objectPool;
}



}
