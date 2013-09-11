package org.mariella.oxygen.service.invocation.http.internal;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.oxygen.service.invocation.http.HttpServiceInvoker;
import org.mariella.oxygen.service.invocation.http.ResolvingObjectInputStreamExtension;
import org.mariella.oxygen.service.server.spring.DefaultServiceExecutionContext;
import org.mariella.oxygen.service.server.spring.EntityAsPoolIdentity;
import org.mariella.oxygen.service.server.spring.SerializableException;
import org.mariella.oxygen.service.server.spring.ServiceExecutionContextHolder;
import org.mariella.oxygen.service.server.spring.ServiceResponse;
import org.mariella.oxygen.service.server.spring.ServiceTransactionTemplate;
import org.mariella.oxygen.spring.OxyDataSourceEntityManagerProviderContext;
import org.mariella.oxygen.spring.OxyEntityManagerInitializer;
import org.mariella.oxygen.spring.OxyEntityManagerProvider;
import org.mariella.persistence.mapping.SchemaMapping;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.CodebaseAwareObjectInputStream;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;


public class OxyHttpServiceInvoker extends HttpInvokerServiceExporter implements HttpServiceInvoker {
	class OxyEntityManagerInitializerImpl implements OxyEntityManagerInitializer {

		OxyServerEntityManager entityManager;
		OxyObjectPool objectPool;

		public OxyEntityManagerInitializerImpl(OxyObjectPool objectPool) {
			this.objectPool = objectPool;
		}

		@SuppressWarnings("unchecked")
		public void initializeEntityManager(OxyServerEntityManager entityManager) {
			this.entityManager = entityManager;
			if (objectPool != null) {
				entityManager.setObjectPool(objectPool);
				//((OxyObjectPoolImpl)entityManager.getObjectPool()).assertConsistent();
				List<String> history = (List<String>)entityManager.getObjectPool().getAttribute("history");
				if(history == null) {
					history = new ArrayList<String>();
					entityManager.getObjectPool().setAttribute("history", history);
				}
				history.add("attached to server entitymanager at " + new Date().toString());
			}
		}

	}


	class ReplacingObjectOutputStream extends ObjectOutputStream {
		private final OxyHttpServiceInvocationContext context;

		public ReplacingObjectOutputStream(OutputStream out, OxyHttpServiceInvocationContext context) throws IOException {
			super(out);
			this.context = context;
			enableReplaceObject(true);
		}

		@Override
		protected Object replaceObject(Object obj) throws IOException {
			boolean objectPoolAvailable = context != null && context.getEntityManager() != null && context.getEntityManager().getObjectPool() != null;
			if (!objectPoolAvailable)
				return obj;
			if (obj == null)
				return null;
			if (schemaMapping.getClassMapping(obj.getClass().getName()) == null)
				return obj;
					
			OxyObjectPool objectPool = context.getEntityManager().getObjectPool();
			
			if (objectPool.getModificationTracker().getIdentity(obj) == null)
				return obj;
			
			// check if entity and object pool is given and entity in object pool 
			EntityState state = context.getEntityManager().getObjectPool().getEntityState(obj);
			if(state != null) {
				EntityAsPoolIdentity id = new EntityAsPoolIdentity();
				id.poolIdentity = state.getPoolIdentity();
				return id;
			}
			return obj;
		}
	}

	class ResolvingObjectInputStream extends CodebaseAwareObjectInputStream {

		public ResolvingObjectInputStream(InputStream in,
				ClassLoader classLoader, String codebaseUrl) throws IOException {
			super(in, classLoader, codebaseUrl);
			enableResolveObject(true);
		}

		@Override
		protected Object resolveObject(Object obj) throws IOException {
			ResolvingObjectInputStreamExtension extension = getResolvingObjectInputStreamExtension();
			if(extension == null) {
				extension = defaultExtension;
			}
			return extension.replaceObject(obj);
		}

	}

	private ServiceExecutionContextHolder serviceExecutionContextHolder;
	private OxyEntityManagerProvider entityManagerProvider;
	private ServiceTransactionTemplate transactionTemplate;
	private SchemaMapping schemaMapping;

	private final ResolvingObjectInputStreamExtension defaultExtension = new ResolvingObjectInputStreamExtension() {
		@Override
		public Object replaceObject(Object object) {
			return object;
		}
	};
	private ResolvingObjectInputStreamExtension resolvingObjectInputStreamExtension = defaultExtension;


	@Override
	public void invoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
		handleRequest(request, response);
	}

	protected RemoteInvocationResult invokeAndCreateResult(final RemoteInvocation invocation, final Object targetObject) {
		serviceExecutionContextHolder.putContext(new DefaultServiceExecutionContext());
		OxyObjectPool objectPool = (OxyObjectPool) invocation.getAttribute("objectPool");
		final OxyEntityManagerInitializerImpl emInitializer = new OxyEntityManagerInitializerImpl(objectPool);
		try {
			OxyDataSourceEntityManagerProviderContext.putOxyEntityManagerInitializerForCurrentThread(emInitializer);

			if (invocation.getMethodName().equals("_getSchemaDescription_")) {
				return new RemoteInvocationResult(serialize(schemaMapping.getSchemaDescription()));
			}
			Method method = getMethod(invocation.getMethodName(), invocation.getParameterTypes());
			Object primitiveResult = method.invoke(targetObject, invocation.getArguments());

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ReplacingObjectOutputStream roos = new ReplacingObjectOutputStream(bos, new OxyHttpServiceInvocationContext() {
				public OxyServerEntityManager getEntityManager() {
					return emInitializer.entityManager;
				}
			});
			roos.writeObject(primitiveResult);

			ServiceResponse serviceResponse = new ServiceResponse();
			serviceResponse.setNativeResult(bos.toByteArray());
			serviceResponse.setObjectPool(objectPool);

			RemoteInvocationResult result = new RemoteInvocationResult(serialize(serviceResponse));
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			return new RemoteInvocationResult(convertToSerializableExceptionIfNeeded(t));
		} finally {
			serviceExecutionContextHolder.removeContext();
			OxyDataSourceEntityManagerProviderContext.removeOxyEntityManagerInitializerForCurrentThread();
		}
	}

	private Throwable convertToSerializableExceptionIfNeeded(Throwable t) {
		if (!isSerializable(t))
			return new SerializableException(t.getMessage(), t.getStackTrace());
		return t;
	}

	private boolean isSerializable(Throwable t) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new ByteArrayOutputStream());
			oos.writeObject(t);
			return true;
		} catch (Throwable x) {
			return false;
		}
	}

	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		serviceExecutionContextHolder.putContext(new DefaultServiceExecutionContext());
		try  {
			schemaMapping = (SchemaMapping) transactionTemplate.execute(new TransactionCallback<Object>() {
				public Object doInTransaction(TransactionStatus status) {
					return entityManagerProvider.getEntityManager().getSchemaMapping();
				}
			});
		} finally {
			serviceExecutionContextHolder.removeContext();
		}
	}

	public void setTransactionTemplate(ServiceTransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void setServiceExecutionContextHolder(ServiceExecutionContextHolder serviceExecutionContextHolder) {
		this.serviceExecutionContextHolder = serviceExecutionContextHolder;
	}

	public OxyEntityManagerProvider getEntityManagerProvider() {
		return entityManagerProvider;
	}

	public void setEntityManagerProvider(OxyEntityManagerProvider entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
	}

	@SuppressWarnings("unchecked")
	private Method getMethod(String methodName, Class<?>[] parameterTypes) throws Exception  {
		return getServiceInterface().getMethod(methodName, parameterTypes);
	}

	private byte[] serialize(Object o) throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream zipos = new GZIPOutputStream(bos);
		ObjectOutputStream oos = new ObjectOutputStream(zipos);
		oos.writeObject(o);
		oos.close();
		zipos.close();
		return bos.toByteArray();
	}

	@Override
	protected ObjectInputStream createObjectInputStream(InputStream is)	throws IOException {
		return new ResolvingObjectInputStream(is, getBeanClassLoader(), null);
	}

	public ResolvingObjectInputStreamExtension getResolvingObjectInputStreamExtension() {
		return resolvingObjectInputStreamExtension;
	}

	public void setResolvingObjectInputStreamExtension(
			ResolvingObjectInputStreamExtension resolvingObjectInputStreamExtension) {
		this.resolvingObjectInputStreamExtension = resolvingObjectInputStreamExtension;
	}
}
