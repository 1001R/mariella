package org.mariella.oxygen.service.invocation.http;

public class HttpServiceRegistration {

	private final String serviceName;
	private final Class<?> serviceInterface;
	private final Object serviceImpl;

	public HttpServiceRegistration(String serviceName, Class<?> serviceInterface, Object serviceImpl) {
		this.serviceName = serviceName;
		this.serviceInterface = serviceInterface;
		this.serviceImpl = serviceImpl;
	}

	public String getServiceName() {
		return serviceName;
	}

	public Class<?> getServiceInterface() {
		return serviceInterface;
	}

	public Object getServiceImpl() {
		return serviceImpl;
	}

	@Override
	public String toString() {
		return getServiceName();
	}

}
