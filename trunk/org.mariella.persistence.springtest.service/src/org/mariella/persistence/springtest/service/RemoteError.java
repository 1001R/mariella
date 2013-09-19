package org.mariella.persistence.springtest.service;
import java.io.Serializable;

public class RemoteError implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String message;
	private final Throwable cause;
	
	public RemoteError(Throwable cause) {
		this(cause.getMessage(), cause);
	}
	
	public RemoteError(String message, Throwable cause) {
		this.message = message;
		this.cause = cause;
	}	
	
	public String getMessage() {
		return message;
	}
	
	public Throwable getCause() {
		return cause;
	}

}
