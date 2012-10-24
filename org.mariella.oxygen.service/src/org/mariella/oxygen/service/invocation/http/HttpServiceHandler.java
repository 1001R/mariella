package org.mariella.oxygen.service.invocation.http;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class HttpServiceHandler {

	private HttpServiceRegistry serviceRegistry;
	private HttpServiceInvokerFactory serviceInvokerFactory;
	private List<ServiceRegistration> registeredServices = new ArrayList<ServiceRegistration>();

	public HttpServiceHandler(HttpServiceInvokerFactory serviceInvokerFactory) {
		this.serviceInvokerFactory = serviceInvokerFactory;
	}

	public HttpServiceHandler(HttpServiceInvokerFactory serviceInvokerFactory, HttpServiceRegistry serviceRegistry) {
		this.serviceInvokerFactory = serviceInvokerFactory;
		this.serviceRegistry = serviceRegistry;
	}

	public void setFactory(HttpServiceInvokerFactory serviceInvokerFactory) {
		this.serviceInvokerFactory = serviceInvokerFactory;
	}

	public HttpServiceInvokerFactory getServiceInvokerFactory() {
		return serviceInvokerFactory;
	}

	public void setServiceRegistry(HttpServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public HttpServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void registerRemoteService(BundleContext context, String serviceName, Class<?> serviceInterface, Object serviceImpl) {
		// create invoker for service and register to OSGi
		HttpServiceInvoker invoker = serviceInvokerFactory.createServiceInvoker(serviceInterface, serviceImpl);
		Dictionary properties = new Properties();
		properties.put("serviceName", serviceName);
		registeredServices.add(context.registerService(HttpServiceInvoker.class.getName(), invoker, properties));
	}

	public void registerRemoteServices(BundleContext context) {
		if(serviceRegistry != null) {
			for(HttpServiceRegistration service : serviceRegistry.getServices()) {
				registerRemoteService(context, service.getServiceName(),
						service.getServiceInterface(), service.getServiceImpl());
			}
		}
	}

	public void unregisterRemoteServices() {
		for(ServiceRegistration registeredService : registeredServices) {
			try {
				registeredService.unregister();
			} catch (IllegalStateException ex) {
				// do nothing!
			}
		}
		registeredServices.clear();
	}

}
