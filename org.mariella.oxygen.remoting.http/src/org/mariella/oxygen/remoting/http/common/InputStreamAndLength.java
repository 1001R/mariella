package org.mariella.oxygen.remoting.http.common;

import java.io.InputStream;
import java.io.Serializable;


public class InputStreamAndLength implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// replaced by InputStreamPlaceholder, needs to be non-transient
	private InputStream inputStream;
	private long length;
	private transient InputStreamAndLengthObserver observer = null;
	
	public InputStreamAndLength(InputStream inputStream, long inputStreamLength) {
		super();
		this.inputStream = inputStream;
		this.length = inputStreamLength;
	}

	public InputStreamAndLength() {
		super();
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public long getLength() {
		return length;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStreamAndLengthObserver getObserver() {
		return observer;
	}

	public synchronized void setObserver(InputStreamAndLengthObserver observer) {
		this.observer = observer;
	}
	
	public synchronized void streamingAborted() {
		if (observer != null)
			observer.streamingAborted();
	}

	public void setLength(long length) {
		this.length = length;
	}

}
