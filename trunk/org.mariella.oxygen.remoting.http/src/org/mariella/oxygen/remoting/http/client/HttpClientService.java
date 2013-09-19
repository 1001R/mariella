package org.mariella.oxygen.remoting.http.client;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.mariella.oxygen.remoting.common.Invoker;
import org.mariella.oxygen.remoting.http.common.HttpRemoting;

public abstract class HttpClientService {
	
	static final Logger log = Logger.getLogger(HttpClientService.class.getName());
	
	protected HttpClient httpClient = null;

	private static Map<Thread,Boolean> threadToReconnect = new WeakHashMap<>();


public void invoke(Invoker<?> invoker, URL url, boolean tryReconnect) throws InterruptedException {
	if (!HttpRemoting.getInstance().running) {
		throw new IllegalStateException(HttpRemoting.class.getName() + " is not running.");
	}
	
	System.out.println("Execute " + invoker.toString());
	if (invoker.getObjectPool() != null)
		System.out.println("  POST context objectcount: " + invoker.getObjectPool().getModificationTracker().getParticipants().size());
	long t0 = System.nanoTime();
	try {
		SendAndReceive sendAndReceive = createSendAndReceive(invoker, url, tryReconnect);
		sendAndReceive.sendAndReceive(getHttpClient());
	} catch (SendAndReceiveException e) {
		handleSendAndReceiveException(e);
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

protected SendAndReceive createSendAndReceive(Invoker<?> invoker, URL url, boolean tryReconnect) {
	return new SendAndReceive(url, tryReconnect, invoker);
}


protected void prepareSendAndReceive(SendAndReceive sendAndReceive) {
}



protected abstract void handleSendAndReceiveException(SendAndReceiveException e);



public HttpClient getHttpClient() {
	if (httpClient == null)
		httpClient = createHttpClient();
	return httpClient;
}



protected abstract HttpClient createHttpClient();

public static synchronized void setTryReconnect(boolean tryReconnect) {
	threadToReconnect.put(Thread.currentThread(), tryReconnect);
}

public static synchronized boolean isTryReconnectForCurrentThread() {
	Boolean is = threadToReconnect.get(Thread.currentThread());
	if (is == null)
		return true; // default is to reconnect
	return is;
}
	

}
