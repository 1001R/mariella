package org.mariella.oxygen.remoting.http.client;

public class SendAndReceiveException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	final int httpCode;
	
	public SendAndReceiveException(int httpCode, String message) {
		super(message);
		this.httpCode = httpCode;
	}

	public int getHttpCode() {
		return httpCode;
	}

}
