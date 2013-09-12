package org.mariella.oxygen.remoting.http.client;

import java.io.InputStream;

public class SendAndReceiveResult {
	private final Object result;
	private final InputStream continuingInputStream;

	public SendAndReceiveResult(Object result, InputStream continuingInputStream) {
		super();
		this.result = result;
		this.continuingInputStream = continuingInputStream;
	}

	public Object getResult() {
		return result;
	}

	public InputStream getContinuingInputStream() {
		return continuingInputStream;
	}
}
