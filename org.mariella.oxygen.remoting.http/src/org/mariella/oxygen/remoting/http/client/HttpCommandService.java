package org.mariella.oxygen.remoting.http.client;


import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.basic_impl.BundleClassResolver;
import org.mariella.oxygen.basic_impl.OxyClientEntityManagerImpl;
import org.mariella.oxygen.remoting.common.Invoker;
import org.mariella.oxygen.remoting.http.common.HttpRemoting;
import org.mariella.oxygen.remoting.http.common.RemoteCall;
import org.mariella.oxygen.remoting.http.common.RemoteResponse;
import org.mariella.persistence.schema.SchemaDescription;

public abstract class HttpCommandService <T> {
	
	protected  HttpClient httpClient;
	private static final Logger log = Logger.getLogger(HttpCommandService.class.getName());
	
	protected ClassResolver classResolver;
	
	
public HttpCommandService() {
	classResolver = createClassResolver();
}

public <R> R execute(RemoteCall<?> call) throws Exception {
	if (!HttpRemoting.getInstance().running) {
		throw new IllegalStateException(HttpRemoting.class.getName() + " is not running.");
	}
	synchronized (call.getObjectPool().getModificationTracker()) {
		System.out.println("Execute " + call.getCommand().getClass().getSimpleName());
		System.out.println("  POST context objectcount: " + call.getObjectPool().getModificationTracker().getParticipants().size());
		long t0 = System.nanoTime();
		try {
			SendAndReceiveResult sendAndReceiveResponse = sendAndReceive(call);
			Object basicResult = sendAndReceiveResponse.getResult();
			if(basicResult instanceof RemoteError) {
				throw buildBusinessException((RemoteError) basicResult);
			}
			RemoteResponse response = (RemoteResponse)basicResult;
			
			Invoker<?> invoker = createInvoker();
			invoker.setCommand(call.getCommand());
			invoker.set
			invoker.invoke(outputStream)
			
			R result = new RemoteResponseProcessor<R>(context, response, classResolver, command, 
					sendAndReceiveResponse.getContinuingInputStream()).buildResult();
			return result;
		} catch (SendAndReceiveException e) {
			log.log(Level.SEVERE, "Error during remote call", e);
			throw new BusinessException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Error during remote call", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error during remote call", e);
			throw new RuntimeException(e);
		} finally {
			long t = (System.nanoTime()-t0)/1000000;
			System.out.println("  Time: "+t+" ms");
		}
	}
}

private BusinessException buildBusinessException(RemoteError error) {
	return new BusinessException(error.getMessage(), error.getCause());
}

private ClassResolver createClassResolver() {
	return new BundleClassResolver();
}

private SendAndReceiveResult sendAndReceive(RemoteCall call) {
	call.setTryReconnect(isTryReconnectForCurrentThread());
	try {
		return new SendAndReceive(classResolver).sendAndReceive(getHttpClient(), call);
	} catch (SendAndReceiveException ex) {
		if (ex.getHttpCode() == 401) {
			throw new AuthenticationException(ex.getMessage());
		} else {
			throw ex;
		}
	} catch (Exception ex) {
		throw new RuntimeException(ex);
	}
}

private static Map<Thread,Boolean> threadToReconnect = new WeakHashMap<>();

public static synchronized void setTryReconnect(boolean tryReconnect) {
	threadToReconnect.put(Thread.currentThread(), tryReconnect);
}

public static synchronized boolean isTryReconnectForCurrentThread() {
	Boolean is = threadToReconnect.get(Thread.currentThread());
	if (is == null)
		return true; // default is to reconnect
	return is;
}

private synchronized HttpClient getHttpClient() {
	if (httpClient == null) {
		httpClient = createHttpClient();
	}
	return httpClient;
}

protected abstract HttpClient createHttpClient(); 

public synchronized void release() {
	if (httpClient != null) {
		httpClient.getConnectionManager().shutdown();
	}
}

}
