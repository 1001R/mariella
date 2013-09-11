package org.mariella.oxygen.service.invocation.http;

import org.mariella.oxygen.service.invocation.http.internal.OxyHttpServiceInvoker;
import org.mariella.oxygen.service.server.spring.ServiceExecutionContextHolder;
import org.mariella.oxygen.service.server.spring.ServiceTransactionTemplate;
import org.mariella.oxygen.spring.OxyEntityManagerProvider;


public class HttpServiceInvokerFactory {

	private ServiceExecutionContextHolder serviceExecutionContextHolder;
	private OxyEntityManagerProvider entityManagerProvider;
	private ServiceTransactionTemplate transactionTemplate;
	private ResolvingObjectInputStreamExtension resolvingObjectInputStreamExtension;

	public HttpServiceInvoker createServiceInvoker(Class<?> serviceInterface, Object service) {
		final OxyHttpServiceInvoker invoker = new OxyHttpServiceInvoker();
		invoker.setBeanClassLoader(service.getClass().getClassLoader());
		invoker.setEntityManagerProvider(getEntityManagerProvider());
		invoker.setServiceExecutionContextHolder(getServiceExecutionContextHolder());
		invoker.setTransactionTemplate(getTransactionTemplate());
		invoker.setServiceInterface(serviceInterface);
		invoker.setService(service);
		invoker.setResolvingObjectInputStreamExtension(resolvingObjectInputStreamExtension);
		invoker.afterPropertiesSet();
		return invoker;
	}

	public void setEntityManagerProvider(
			OxyEntityManagerProvider entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
	}

	public void setServiceExecutionContextHolder(
			ServiceExecutionContextHolder serviceExecutionContextHolder) {
		this.serviceExecutionContextHolder = serviceExecutionContextHolder;
	}

	public void setTransactionTemplate(
			ServiceTransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public OxyEntityManagerProvider getEntityManagerProvider() {
		return entityManagerProvider;
	}

	public ServiceExecutionContextHolder getServiceExecutionContextHolder() {
		return serviceExecutionContextHolder;
	}

	public ServiceTransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public ResolvingObjectInputStreamExtension getResolvingObjectInputStreamExtension() {
		return resolvingObjectInputStreamExtension;
	}

	public void setResolvingObjectInputStreamExtension(
			ResolvingObjectInputStreamExtension resolvingObjectInputStreamExtension) {
		this.resolvingObjectInputStreamExtension = resolvingObjectInputStreamExtension;
	}

}
