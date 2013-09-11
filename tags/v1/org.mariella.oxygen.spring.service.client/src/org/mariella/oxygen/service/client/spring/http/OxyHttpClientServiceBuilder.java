package org.mariella.oxygen.service.client.spring.http;

import java.io.DataInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

import org.mariella.oxygen.annotations.RemotableService;
import org.mariella.persistence.annotations.processing.ClasspathBrowser;
import org.mariella.persistence.annotations.processing.ClasspathBrowser.Entry;
import org.osgi.framework.Bundle;
import org.springframework.aop.framework.ProxyFactory;

public class OxyHttpClientServiceBuilder {

	private String baseUrl;
	private Bundle serviceBundle;
	private Bundle clientBundle;
	private Map<Class<?>, String> versionRangeMap = new HashMap<Class<?>, String>();
	private OxyHttpClientService service;
	private ReplacingObjectOutputStreamExtension replacingObjectOutputStreamExtension;
	private OxyClientEntityManagerProvider entityManagerProvider;

	public void build() throws Exception {
		List<Entry> entries = ClasspathBrowser.resolveEntries(serviceBundle.getResource("/"), serviceBundle);
		Map<Class<?>,List<Class<?>>> annotatedClassesMap = readAnnotatedClasses(entries, RemotableService.class);
		System.out.println("OxyHttpClientServiceBuilder.build(): annotatedClassesMap = " + annotatedClassesMap );
		buildRemotableServices(annotatedClassesMap.get(RemotableService.class));
	}

	private void buildRemotableServices(List<Class<?>> remotableServiceClasses) throws Exception {
		for (Class<?> remotableServiceClass : remotableServiceClasses) {
			service.classToServiceMap.put(remotableServiceClass, buildRemotableService(remotableServiceClass));
		}
		service.entityManagerService = (EntityManagerService)buildRemotableService(EntityManagerService.class);
	}

	private Object buildRemotableService(Class<?> remotableServiceClass) throws Exception {
		RemotableService anno = remotableServiceClass.getAnnotation(RemotableService.class);
		final BundleClassLoader classLoader = new BundleClassLoader(clientBundle);

		OxyHttpInvokerClientInterceptor interceptor = new OxyHttpInvokerClientInterceptor();
		if (replacingObjectOutputStreamExtension != null)
			interceptor.setReplacingObjectOutputStreamExtension(replacingObjectOutputStreamExtension);
		if (entityManagerProvider != null)
			interceptor.setEntityManagerProvider(entityManagerProvider);
		
		interceptor.setServiceInterface(remotableServiceClass);
		interceptor.setBeanClassLoader(classLoader);
		interceptor.setServiceUrl(buildServiceUrl(anno.name()));

		return new ProxyFactory(remotableServiceClass, interceptor).getProxy(classLoader);
	}


	private String buildServiceUrl(String name) throws Exception {
		URL url = new URL(baseUrl);
		url = new URL(url, name);
		return url.toExternalForm();
	}

	private Map<Class<?>,List<Class<?>>> readAnnotatedClasses(List<Entry> entries, Class<?> ... annotationClasses) throws Exception {
		// get all class-files
//		ClasspathBrowser browser = ClasspathBrowser.getBrowser(oxyUnitInfo.getPersistenceUnitRootUrl(), bundle);
		Map<Class<?>,List<Class<?>>> result = new HashMap<Class<?>, List<Class<?>>>();
		for (Class<?> annoClass : annotationClasses) {
			result.put(annoClass, new ArrayList<Class<?>>());
		}
		for (Entry entry : entries) {
			DataInputStream dstream = new DataInputStream(entry.getInputStream());
			ClassFile cf = null;
			System.out.println("Parsing " + entry.getName());
			try {
				cf = new ClassFile(dstream);
			} catch (Exception e) {
				throw new RuntimeException("Error during parsing class " + entry.getName(), e);
			} finally {
				dstream.close();
				entry.getInputStream().close();
			}

			if(cf != null && cf.getName().endsWith("ActorService")) {
				for(Object attribute : cf.getAttributes()) {
					System.out.println(attribute);
				}
			}

			AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag );
			if ( visible != null ) {
				for (Class<?> annoClass : annotationClasses) {
					javassist.bytecode.annotation.Annotation anno = visible.getAnnotation(annoClass.getName());
					if (anno != null) {
						List<Class<?>> list = result.get(annoClass);
						if (serviceBundle != null) {
							list.add(serviceBundle.loadClass(cf.getName()));
						} else {
							list.add(Class.forName(cf.getName()));
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Optional in order to define the osgi version range.
	 *
	 * @param remotedServiceClass
	 * @param versionRange
	 */
	public void setVersionRange(Class<?> serviceClass, String versionRange) {
		versionRangeMap.put(serviceClass, versionRange);
	}

	public void setBaseServiceUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setServiceBundle(Bundle bundle) {
		this.serviceBundle = bundle;
	}

	public void setService(OxyHttpClientService service) {
		this.service = service;
	}

	public void setClientBundle(Bundle clientBundle) {
		this.clientBundle = clientBundle;
	}

	public ReplacingObjectOutputStreamExtension getReplacingObjectOutputStreamExtension() {
		return replacingObjectOutputStreamExtension;
	}

	public void setReplacingObjectOutputStreamExtension(
			ReplacingObjectOutputStreamExtension replacingObjectOutputStreamExtension) {
		this.replacingObjectOutputStreamExtension = replacingObjectOutputStreamExtension;
	}

	public OxyClientEntityManagerProvider getEntityManagerProvider() {
		return entityManagerProvider;
	}

	public void setEntityManagerProvider(OxyClientEntityManagerProvider entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
	}



}
