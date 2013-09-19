package org.mariella.oxygen.remoting.http.client;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.mariella.oxygen.remoting.common.InputStreamAndLength;
import org.mariella.oxygen.remoting.common.InputStreamAndLengthObserver;
import org.mariella.oxygen.remoting.common.Invoker;
import org.mariella.oxygen.remoting.http.common.HttpInputStreamWrapper;
import org.mariella.oxygen.remoting.http.common.HttpRemoting;
import org.mariella.oxygen.remoting.http.common.InputStreamPlaceholder;

public class SendAndReceive {
	
	public static final Logger log = Logger.getLogger(SendAndReceive.class.getName());

	class AbortableInputStreamEntity extends InputStreamEntity {
		volatile AbortableInputStreamEntityObserver observer;
		AbortableInputStreamEntity(InputStream instream, long length) {
			super(instream, length);
		}
		void abort() {
			if (observer != null)
				observer.streamingAborted();
		}
	}
	
	interface AbortableInputStreamEntityObserver {
		void streamingAborted();
	}
	
	private static class ObjectStatistic {
		private int count;
		private boolean collection;
		private int empty;
		
		private ObjectStatistic(int count, boolean collection) {
			this.count = count;
			this.collection = collection;
		}
	}
	
	/**
	 * When a call object graph contains an InputStream object, it is replaced
	 * by a placeholder, on server side it is then replaced with the InputStream
	 * of the servlet request.
	 * 
	 * @author Rufus Team Member
	 */
	class ReplacingObjectOutputStream extends ObjectOutputStream {
		private Set<Object> objects = new HashSet<>();
		private Map<String, ObjectStatistic> typeStatistic = new HashMap<>();

		public ReplacingObjectOutputStream(OutputStream out) throws IOException {
			super(out);
			enableReplaceObject(true);
		}
		
		@Override
		protected Object replaceObject(Object obj) throws IOException {
			if (obj instanceof InputStream) {
				obj = new InputStreamPlaceholder();
			}
			if (obj != null) {
				objects.add(obj);
				
				String className;
				if (obj.getClass().isArray()) {
					className = obj.getClass().getComponentType().getName()+"[]";
				} else {
					className = obj.getClass().getName();
				}
				ObjectStatistic statistic = typeStatistic.get(className);
				if (statistic == null) {
					statistic = new ObjectStatistic(0, obj instanceof Collection<?> || obj instanceof Map<?, ?>);
					typeStatistic.put(className, statistic);
				}
				statistic.count++;
				if ((obj instanceof Collection<?> && ((Collection<?>) obj).isEmpty()) || (obj instanceof Map<?, ?> && ((Map<?, ?>) obj).isEmpty())) {
					statistic.empty++;
				}
			}
			return obj;
		}
		
		public int getNumberOfObjects() {
			return objects.size();
		}
		
		public Map<String, ObjectStatistic> getTypeStatistic() {
			return typeStatistic;
		}
	}
	
	private final URL url;
	private final boolean tryReconnect;
	protected final Invoker<?> invoker;
	
	private List<SendAndReceiveListener> listeners = new ArrayList<>();
	private volatile boolean cancelRequest = false;
	private int maxTries = 1;
	private long waitForNextRetry = -1L;
	
public SendAndReceive(URL url, boolean tryReconnect, Invoker<?> invoker) {
	this.url = url;
	this.tryReconnect = tryReconnect;
	this.invoker = invoker;
	if (HttpRemoting.getInstance().getSendAndReceiveInitializer() != null) {
		HttpRemoting.getInstance().getSendAndReceiveInitializer().initialize(this);
	}
}

protected boolean isInputStreamResult() {
	return false;
}

protected void preparePost(HttpPost httpPost) {
}

public void sendAndReceive(HttpClient httpclient) throws SendAndReceiveException, IOException, ClassNotFoundException, InterruptedException {
	
	HttpRemoting.getInstance().addCurrentSendAndReceive(this);
	HttpPost httpPost = null;
	try {
		
		httpPost = new HttpPost(url.toExternalForm());
		HttpEntity requestBody = buildRequestBody();
		
		final HttpPost post = httpPost;
		if (requestBody instanceof AbortableInputStreamEntity) {
			((AbortableInputStreamEntity) requestBody).observer = new AbortableInputStreamEntityObserver() {
				@Override
				public void streamingAborted() {
					post.abort();
				}
			};
		}
		
		httpPost.setEntity(requestBody);
		
		preparePost(httpPost);

		HttpResponse response = executeRetry(httpclient, httpPost);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new SendAndReceiveException(response.getStatusLine().getStatusCode(), response.getStatusLine().toString());
		}
		
		HttpEntity httpEntity = response.getEntity();

		InputStream resultIn = httpEntity.getContent();
		invoker.readResult(resultIn);
		
		if (invoker.getResult() instanceof InputStreamAndLength) {
			InputStreamAndLength isAndLen = (InputStreamAndLength)invoker.getResult();
			isAndLen.setInputStream(new HttpInputStreamWrapper(httpPost, httpEntity));
			invoker.setResult(isAndLen);
		} else {
			// consume the connection immediately if content stream is not needed
			EntityUtils.consume(httpEntity); // put's the connection back to the pool
		}
	} catch (ConnectException ex) {
		if (httpPost != null) {
			httpPost.abort();
		}
		throw new SendAndReceiveException(-1, ex.getMessage());
	} catch (IOException | InterruptedException | ClassNotFoundException ex) {
		if (httpPost != null) {
			httpPost.abort();
		}
		throw ex;
	} finally {
		HttpRemoting.getInstance().removeCurrentSendAndReceive(this);
		fireAboutToLeave();
	}
	
}

private HttpResponse executeRetry(HttpClient httpclient, HttpPost httppost) throws InterruptedException, IOException {
	for (int i=0; i<maxTries && !cancelRequest; i++) {
		try {
			HttpResponse response = httpclient.execute(httppost);
			Header[] repServerTime = response.getHeaders("RepositoryServerTime");
			if (repServerTime != null && repServerTime.length == 1) {
				System.out.println("  Server Time: "+repServerTime[0].getValue()+"ms");
			}
			fireConnectOk(i);
			return response;
		} catch (InterruptedIOException ce) {
			log.log(Level.INFO, "Send/Receive has been canceled");
			throw ce;
		} catch (ConnectException ce) {
			if (!tryReconnect)
				break;
			if (cancelRequest) {
				log.log(Level.INFO, "Could not connect to " + httppost.getURI() + ". Connect has been canceled.");
				fireConnectCanceled(i);
			}
			if (i<maxTries-1) {
				log.log(Level.INFO, "Could not connect to " + httppost.getURI() + ". Will retry after " + (waitForNextRetry / 100L) + " seconds.");
				fireConnectRetryAfterFailed(i);
				try {
					Thread.sleep(waitForNextRetry);
				} catch (InterruptedException e) {
					// InterruptedException appears on shutdown, avoid any subsequent command, otherwise calling plugin could block shutdown sequence...
					HttpRemoting.getInstance().running = false;
					log.log(Level.INFO, "Could not connect to " + httppost.getURI() + ". Retry has been interrupted.");
					throw new ConnectException("Interrupted");
				}
			}
		}
	}
	
	fireConnectFailed(maxTries - 1);
	String msg = "Could not connect to repository.";
	if (tryReconnect && maxTries > 1) {
		msg += " Number of tries: " + maxTries + ", waiting " + (waitForNextRetry / 1000L) + " seconds each time.";
	}
	throw new ConnectException(msg);
}

private void fireConnectCanceled(int attempt) {
	for (SendAndReceiveListener l : listeners)
		l.connectCanceled(this, attempt);
}

private void fireConnectRetryAfterFailed(int attempt) {
	for (SendAndReceiveListener l : listeners)
		l.connectRetryAfterFailed(this, attempt);
}

private void fireConnectFailed(int attempt) {
	for (SendAndReceiveListener l : listeners)
		l.connectFailed(this, attempt);
}

private void fireConnectOk(int attempt) {
	for (SendAndReceiveListener l : listeners)
		l.connectOk(this, attempt);
}

private void fireAboutToLeave() {
	for (SendAndReceiveListener l : listeners)
		l.aboutToLeave(this);
}

public void cancelConnect() {
	cancelRequest = true;
	Thread.currentThread().interrupt();
}

private HttpEntity buildRequestBody() throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ReplacingObjectOutputStream oos = new ReplacingObjectOutputStream(out);

	invoker.invoke(oos);
	oos.close();
	byte[] callBytes = out.toByteArray();
	
	if (invoker.getPostedContent() != null) {
		InputStream in1 = new ByteArrayInputStream(callBytes);
		InputStreamAndLength abortable = invoker.getPostedContent();
		InputStream in2 = abortable.getInputStream();
		final AbortableInputStreamEntity entity = new AbortableInputStreamEntity(new SequenceInputStream(in1, in2), 
				invoker.getPostedContent().getLength() + callBytes.length);
		abortable.setObserver(new InputStreamAndLengthObserver() {
			@Override
			public void streamingAborted() {
				entity.abort();
			}
		});
		return entity;
	}
	
	System.out.println("  POST objectcount: "+oos.getNumberOfObjects());
	System.out.println("  POST size: "+callBytes.length+" bytes");
	List<String> types = new ArrayList<>(oos.getTypeStatistic().keySet());
	Collections.sort(types);
	for (String type : types) {
		ObjectStatistic statistic = oos.getTypeStatistic().get(type);
		String s = String.format("    %100s %d times", type, statistic.count);
		if (statistic.collection) {
			s += ", "+statistic.empty+" empty";
		}
		System.out.println(s);
	}
//		String s = new String(callBytes);
	return new ByteArrayEntity(callBytes);
}

public void addListener(SendAndReceiveListener l) {
	listeners.add(l);
}

public void removeListener(SendAndReceiveListener l) {
	listeners.remove(l);
}

public int getMaxRetries() {
	return maxTries;
}

public void setMaxRetries(int maxRetries) {
	this.maxTries = maxRetries;
}

public long getWaitForNextRetry() {
	return waitForNextRetry;
}

public void setWaitForNextRetry(long waitForNextRetry) {
	this.waitForNextRetry = waitForNextRetry;
}

}
