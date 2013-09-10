package org.mariella.oxygen.service.client.spring.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

import org.aopalliance.intercept.MethodInvocation;
import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.service.server.spring.ServiceResponse;
import org.mariella.persistence.schema.SchemaDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

public class OxyHttpInvokerClientInterceptor extends HttpInvokerClientInterceptor {

	private class ExtendedHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {

		@Override
		protected void prepareConnection(HttpURLConnection con, int contentLength)
				throws IOException {
			super.prepareConnection(con, contentLength);
			if(versionRange != null) {
				con.setRequestProperty("versionRange", versionRange);
			}
		}
		
		@Override
		protected void writeRemoteInvocation(RemoteInvocation invocation,
				OutputStream os) throws IOException {
			ReplacingObjectOutputStream oos = new ReplacingObjectOutputStream(decorateOutputStream(os));
			try {
				doWriteRemoteInvocation(invocation, oos);
			}
			finally {
				oos.close();
			}
			
		}
		
	}
	
	class ObjectInputStreamImpl extends ObjectInputStream {
		public ObjectInputStreamImpl(InputStream in) throws IOException {
			super(in);
		}

		protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
		    return Class.forName(desc.getName(), false, getBeanClassLoader());
		}
	}
	
	class ReplacingObjectOutputStream extends ObjectOutputStream {

		public ReplacingObjectOutputStream(OutputStream out) throws IOException {
			super(out);
			enableReplaceObject(true);
		}
		
		@Override
		protected Object replaceObject(Object obj) throws IOException {
			return replacingObjectOutputStreamExtension.replaceObject(obj);
		}
	}
	
	private HttpInvokerRequestExecutor httpInvokerRequestExecutor;
	private String versionRange;
	private OxyClientEntityManagerProvider entityManagerProvider;
	private ReplacingObjectOutputStreamExtension replacingObjectOutputStreamExtension = new ReplacingObjectOutputStreamExtension() {
		
		public Object replaceObject(Object obj) {
			return obj;
		}
	};

	@Override
	public HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
		if (this.httpInvokerRequestExecutor == null) {
			ExtendedHttpInvokerRequestExecutor executor = new ExtendedHttpInvokerRequestExecutor();
			executor.setBeanClassLoader(getBeanClassLoader());
			executor.setAcceptGzipEncoding(true);
			this.httpInvokerRequestExecutor = executor;
		}
		return this.httpInvokerRequestExecutor;
	}
	
	public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
		if (methodInvocation.getMethod().getName().equals("getSchemaDescription"))
			return readSchemaDescription();

		//((OxyObjectPoolImpl)OxyClientServiceExecutionContext.getOxyEntityManagerForCurrentThread().getObjectPool()).assertConsistent();
		final OxyHttpClientMethodInvocation oxyInvocation = new OxyHttpClientMethodInvocation(methodInvocation);
		OxyEntityManager entityManager = fetchEntityManager(methodInvocation);
		if (entityManager != null)
			oxyInvocation.setObjectPool(entityManager.getObjectPool());

		byte[] bytes = (byte[])super.invoke(oxyInvocation);
		ServiceResponse resp = (ServiceResponse)deserialize(bytes);
		ServiceResponseProcessor proc = createResponseProcessor(resp);
		Object result = proc.processResponse(new OxyHttpClientServiceInvocationContext() {
				public OxyEntityManager getEntityManager() {
					return fetchEntityManager(methodInvocation);
				}
				public ClassLoader getClassLoader() {
					return getBeanClassLoader();
				}
			},
			resp);
		return result;
	}
	
	private OxyEntityManager fetchEntityManager(MethodInvocation methodInvocation) {
		OxyEntityManager entityManager = null;
		if (entityManagerProvider != null)
			entityManager = entityManagerProvider.getEntityManager(methodInvocation);
		else
			entityManager = OxyClientServiceExecutionContext.getOxyEntityManagerForCurrentThread();
		
		return entityManager;
	}

	protected RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
		RemoteInvocation inv = super.createRemoteInvocation(methodInvocation);
		inv.addAttribute("objectPool", ((OxyHttpClientMethodInvocation)methodInvocation).getObjectPool());
		return inv;
	}

	private SchemaDescription readSchemaDescription() throws Throwable {
		RemoteInvocation invocation = new RemoteInvocation("_getSchemaDescription_", null, null);
		SchemaDescription schemaDescription = (SchemaDescription)deserialize((byte[])((RemoteInvocationResult)executeRequest(invocation)).getValue());
		schemaDescription.afterDeserialization(getBeanClassLoader());
		return schemaDescription;
	}

	private ServiceResponseProcessor createResponseProcessor(ServiceResponse resp) {
		if (resp == null)
			return null;

		return new ServiceResponseProcessor();
	}

	private Object deserialize(byte[] bytes) throws Throwable {
		GZIPInputStream zipis = new GZIPInputStream(new ByteArrayInputStream(bytes));
		ObjectInputStream ois = new ObjectInputStreamImpl(zipis);
		Object o = ois.readObject();
		return o;
	}

	public void setVersionRange(String versionRange) {
		this.versionRange = versionRange;
	}

	public String getVersionRange() {
		if(versionRange == null) {
			versionRange = resolveVersion();
		}
		return versionRange;
	}

	private String resolveVersion() {
		// default - use bundle-version of serviceInterface
		if(getServiceInterface() != null) {
			ClassLoader cl = getServiceInterface().getClassLoader();
			if(cl instanceof BundleReference) {
				Bundle bundle = ((BundleReference) cl).getBundle();
				return bundle.getVersion().toString();
			}
		}
		return null;
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
