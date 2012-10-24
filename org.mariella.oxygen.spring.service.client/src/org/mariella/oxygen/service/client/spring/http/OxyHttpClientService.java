package org.mariella.oxygen.service.client.spring.http;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.basic_impl.EntityManagerModificationTracker;
import org.mariella.oxygen.basic_impl.OxyClientEntityManagerImpl;
import org.mariella.oxygen.basic_impl.OxyObjectPoolImpl;
import org.mariella.persistence.runtime.RIListener;
import org.mariella.persistence.schema.SchemaDescription;

public class OxyHttpClientService {
	protected Map<Class<?>,Object> classToServiceMap = new HashMap<Class<?>, Object>();
	protected EntityManagerService entityManagerService;
	private SchemaDescription schemaDescription;
	private ClassResolver classResolver;
	
	@SuppressWarnings("unchecked")
	public <S> S getService(Class<S> serviceClass) {
		return (S) classToServiceMap.get(serviceClass);
	}
	
	public OxyEntityManager createEntityManager(boolean readonly) {
		EntityManagerModificationTracker tracker = new EntityManagerModificationTracker();
		tracker.addPersistentListener(new RIListener(tracker));
		OxyEntityManager em = createEntityManager(new OxyObjectPoolImpl(tracker));
		tracker.setEntityManager(em);
		return em;
	}
	
	public OxyObjectPool createObjectPool(boolean readOnly) {
		return createEntityManager(readOnly).getObjectPool();
	}

	@SuppressWarnings("unchecked")
	private OxyEntityManager createEntityManager(OxyObjectPool oxyObjectPool) {
		Thread current = Thread.currentThread();
		ClassLoader oldLoader = current.getContextClassLoader();
		try {
			current.setContextClassLoader(getClass().getClassLoader());

			OxyEntityManager clientEntityManager = new OxyClientEntityManagerImpl(schemaDescription,
					classResolver);
			List<String> history = (List<String>)clientEntityManager.getObjectPool().getAttribute("history");
			if(history == null) {
				history = new ArrayList<String>();
				clientEntityManager.getObjectPool().setAttribute("history", history);
			}
			history.add("created on client at " + new Date().toString());

			clientEntityManager.setObjectPool(oxyObjectPool);
			return clientEntityManager;
		} finally {
			current.setContextClassLoader(oldLoader);
		}
	}

	public void start() {
		schemaDescription = entityManagerService.getSchemaDescription();
	}

	public ClassResolver getClassResolver() {
		return classResolver;
	}

	public void setClassResolver(ClassResolver classResolver) {
		this.classResolver = classResolver;
	}

	public SchemaDescription getSchemaDescription() {
		return schemaDescription;
	}

}
