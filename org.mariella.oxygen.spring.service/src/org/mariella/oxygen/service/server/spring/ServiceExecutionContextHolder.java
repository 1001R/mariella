package org.mariella.oxygen.service.server.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceExecutionContextHolder {

private Map<Thread, ServiceExecutionContext> threadToContext = new ConcurrentHashMap<Thread, ServiceExecutionContext>();

public void putContext(ServiceExecutionContext ctx) {
	threadToContext.put(Thread.currentThread(), ctx);
}

public ServiceExecutionContext getContext() {
	ServiceExecutionContext ctx = threadToContext.get(Thread.currentThread());
	if (ctx == null)
		throw new NoServiceExecutionContextException();
	return ctx;
}

public void removeContext() {
	threadToContext.remove(Thread.currentThread());
}

}
