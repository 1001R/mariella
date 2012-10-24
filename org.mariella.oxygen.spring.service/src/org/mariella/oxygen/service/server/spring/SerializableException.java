package org.mariella.oxygen.service.server.spring;

public class SerializableException extends Throwable {
private static final long serialVersionUID = 1L;

StackTraceElement[] stackTrace;

public SerializableException(String msg, StackTraceElement[] stackTrace) {
	super(msg);
	this.stackTrace = stackTrace;
}

public StackTraceElement[] getStackTrace() {
	return stackTrace;
}

}
