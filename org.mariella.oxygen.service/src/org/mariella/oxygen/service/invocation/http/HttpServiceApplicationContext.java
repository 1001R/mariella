package org.mariella.oxygen.service.invocation.http;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class HttpServiceApplicationContext extends ClassPathXmlApplicationContext {

	private static final String SERVICE_REGISTRY = "serviceRegistry";
	private static final String SERVICE_INVOKER_FACTORY = "serviceInvokerFactory";

	public HttpServiceApplicationContext(String configLocation) {
		super(configLocation);
	}

	public abstract ClassLoader getBundleClassLoader();

	@Override
	public ClassLoader getClassLoader() {
		ClassLoader cl = getBundleClassLoader();
		return cl != null ? cl : super.getClassLoader();
	}

	@Override
	protected void onRefresh() throws BeansException {
		super.onRefresh();
		initServiceRegistry();
	}

	protected void initServiceRegistry() {
		if(!getBeanFactory().containsSingleton(SERVICE_REGISTRY)) {
			getBeanFactory().registerSingleton(SERVICE_REGISTRY, createServiceRegistry());
		}
	}

	protected HttpServiceRegistry createServiceRegistry() throws BeansException {
		final HttpServiceRegistry serviceRegistry = new HttpServiceRegistry();
		final List<HttpServiceRegistration> services = new ArrayList<HttpServiceRegistration>();
		final ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		for(String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
			if(beanDefinition instanceof BeanMetadataAttributeAccessor) {
				BeanMetadataAttribute attrServiceInterface = ((BeanMetadataAttributeAccessor)
						beanDefinition).getMetadataAttribute("serviceInterface");
				if(attrServiceInterface != null) {
					String serviceInterfaceName = (String) attrServiceInterface.getValue();
					Class<?> serviceInterface;
					try {
						serviceInterface = getClassLoader().loadClass(serviceInterfaceName);
					} catch (ClassNotFoundException ex) {
						throw new CannotLoadBeanClassException(beanDefinition.getResourceDescription(),
								beanDefinitionName, beanDefinition.getBeanClassName(), ex);
					}
					String serviceName = beanDefinitionName;
					// 'serviceName' is optional
					BeanMetadataAttribute attrServiceName = ((BeanMetadataAttributeAccessor)
							beanDefinition).getMetadataAttribute("serviceName");
					if(attrServiceName != null) {
						serviceName = (String) attrServiceName.getValue();
					}
					services.add(new HttpServiceRegistration(serviceName, serviceInterface,
							beanFactory.getBean(beanDefinitionName)));
				}
			}
		}
		serviceRegistry.setServices(services);
		return serviceRegistry;
	}

	public HttpServiceRegistry getServiceRegistry() {
		return (HttpServiceRegistry) getBeanFactory().getSingleton(SERVICE_REGISTRY);
	}

	public HttpServiceInvokerFactory getServiceInvokerFactory() {
		return (HttpServiceInvokerFactory) getBeanFactory().getSingleton(SERVICE_INVOKER_FACTORY);
	}

}
