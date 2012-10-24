package org.mariella.oxygen.service.invocation.http;

import java.util.List;


public class HttpServiceRegistry {

	private List<HttpServiceRegistration> services;

	public HttpServiceRegistry() {
	}

	public void setServices(List<HttpServiceRegistration> services) {
		this.services = services;
	}

	public List<HttpServiceRegistration> getServices() {
		return services;
	}

}
