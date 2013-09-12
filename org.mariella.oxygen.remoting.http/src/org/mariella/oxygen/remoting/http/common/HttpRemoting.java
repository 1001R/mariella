package org.mariella.oxygen.remoting.http.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.mariella.oxygen.remoting.http.client.SendAndReceive;
import org.mariella.oxygen.remoting.http.client.SendAndReceiveInitializer;
import org.osgi.framework.BundleContext;


public class HttpRemoting extends Plugin {
	private static HttpRemoting instance;
	
	public volatile boolean running = true;
	private SendAndReceiveInitializer sendAndReceiveInitializer = null;
	private List<SendAndReceive> currentSendAndReceives = new ArrayList<>();

public void start(BundleContext bundleContext) throws Exception {
	HttpRemoting.instance = this;
	running = true;
}

public void stop(BundleContext bundleContext) throws Exception {
	running = false;
	synchronized(this) {
		for (SendAndReceive snr : currentSendAndReceives)
			snr.cancelConnect();
	}
	HttpRemoting.instance = null;
}

public static HttpRemoting getInstance() {
	return instance;
}

public SendAndReceiveInitializer getSendAndReceiveInitializer() {
	return sendAndReceiveInitializer;
}

public void setSendAndReceiveInitializer(
		SendAndReceiveInitializer sendAndReceiveInitializer) {
	this.sendAndReceiveInitializer = sendAndReceiveInitializer;
}

public synchronized void addCurrentSendAndReceive(SendAndReceive snr) {
	currentSendAndReceives.add(snr);
}

public synchronized void removeCurrentSendAndReceive(SendAndReceive snr) {
	currentSendAndReceives.remove(snr);
}

}
