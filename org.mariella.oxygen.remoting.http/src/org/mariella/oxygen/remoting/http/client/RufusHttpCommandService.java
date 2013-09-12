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

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.basic_impl.OxyClientEntityManagerImpl;
import org.mariella.persistence.schema.SchemaDescription;

public class RufusHttpCommandService<T> extends HttpCommandService<T> {
	private static SchemaDescription cachedSchemaDescription = null;

	private ClassLoader classLoader = new ClassLoader() {
		public java.lang.Class<?> loadClass(String name) throws ClassNotFoundException {
			return classResolver.resolveClass(name);
		}
	}; 

@Override
protected HttpClient createHttpClient() {
	boolean https = false;
	try {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
//				schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		if (https) {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(SendAndReceive.class.getResourceAsStream("/keystore"), "rufuspass".toCharArray());
			SSLSocketFactory socketFactory = new SSLSocketFactory("TLS", keyStore, "rufuspass", null, new SecureRandom(), 
					new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
					return true;
				}
			}, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", 443, socketFactory));
		}
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(schemeRegistry);
		connectionManager.setMaxTotal(10);
		connectionManager.setDefaultMaxPerRoute(5);
//	        	connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("locahost", 80)), 5);
		HttpClient httpClient = new DefaultHttpClient(connectionManager);
		return httpClient;
	} catch (NoSuchAlgorithmException | CertificateException | IOException | 
			KeyManagementException | UnrecoverableKeyException | KeyStoreException ex) {
		throw new RuntimeException("Error creating HTTP client.", ex);
	}
}

private SchemaDescription loadSchemaDescription(AppContext appContext) {
	synchronized (HttpCommandService.class) {
		if (cachedSchemaDescription != null) {
			return cachedSchemaDescription;
		}
		Repository repository = appContext.getRepository();
		LoadSchemaDescriptionCall call = new LoadSchemaDescriptionCall(appContext.getCredentials(), repository.getUrl());
		SendAndReceiveResult result = sendAndReceive(call);
		SchemaDescription sd = (SchemaDescription)result.getResult();
		sd.afterDeserialization(classLoader);
		cachedSchemaDescription = sd;
		return sd;
	}
}

@Override
public PoolContext createPoolContext(AppContext appContext) {
	OxyEntityManager clientEntityManager;
	SchemaDescription schemaDescr = loadSchemaDescription(appContext);
	clientEntityManager = new OxyClientEntityManagerImpl(schemaDescr, classResolver);
	return new DefaultPoolContext(clientEntityManager.getObjectPool(), appContext);
}

public void authenticate(String url, String username, String password) throws AuthenticationException {
	AuthenticationCall call = new AuthenticationCall(url, username, password);
	try {
		new SendAndReceive(classResolver).sendAndReceive(getHttpClient(), call);
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



}
