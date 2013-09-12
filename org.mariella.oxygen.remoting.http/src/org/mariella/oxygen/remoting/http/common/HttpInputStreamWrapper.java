package org.mariella.oxygen.remoting.http.common;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

class HttpInputStreamWrapper extends InputStream {

	private HttpPost httpPost;
	private HttpEntity httpEntity;
	private InputStream content;
	
	HttpInputStreamWrapper(HttpPost httpPost, HttpEntity httpEntity) throws IllegalStateException, IOException {
		this.httpPost = httpPost;
		this.httpEntity = httpEntity;
		this.content = httpEntity.getContent();
	}

	@Override
	public int read() throws IOException {
		try {
			return content.read();
		} catch (IOException ex) {
			httpPost.abort();
			throw ex;
		}
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		try {
			return content.read(b);
		} catch (IOException ex) {
			httpPost.abort();
			throw ex;
		}
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			return content.read(b, off, len);
		} catch (IOException ex) {
			httpPost.abort();
			throw ex;
		}
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		try {
			content.mark(readlimit);
		} catch (Throwable ex) {
			httpPost.abort();
		}
	}
	
	@Override
	public synchronized void reset() throws IOException {
		try {
			content.reset();
		} catch (IOException ex) {
			httpPost.abort();
			throw ex;
		}
	}
	
	@Override
	public long skip(long n) throws IOException {
		try {
			return content.skip(n);
		} catch (IOException ex) {
			httpPost.abort();
			throw ex;
		}
	}
	
	@Override
	public boolean markSupported() {
		try {
			return content.markSupported();
		} catch (Throwable ex) {
			httpPost.abort();
			throw ex;
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			EntityUtils.consume(httpEntity);
		} catch (IOException ex) {
			httpPost.abort();
			throw ex;
		}
	}
	
	@Override
	public int available() throws IOException {
		try {
			return content.available();
		} catch (IOException ex) {
			httpPost.abort();
			throw ex;
		}
	}

}
