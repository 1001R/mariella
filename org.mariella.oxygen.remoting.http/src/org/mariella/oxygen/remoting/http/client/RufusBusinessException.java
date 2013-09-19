package org.mariella.oxygen.remoting.http.client;

@SuppressWarnings("serial")
public class RufusBusinessException extends RuntimeException {

	public RufusBusinessException() {
	}

	public RufusBusinessException(String message) {
		super(message);
	}

	public RufusBusinessException(Throwable cause) {
		super(cause);
	}

	public RufusBusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public RufusBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
