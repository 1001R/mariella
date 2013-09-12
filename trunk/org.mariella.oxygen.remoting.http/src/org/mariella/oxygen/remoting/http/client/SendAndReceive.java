package org.mariella.oxygen.remoting.http.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
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
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.remoting.http.common.HttpInputStreamWrapper;
import org.mariella.oxygen.remoting.http.common.HttpRemoting;
import org.mariella.oxygen.remoting.http.common.InputStreamAndLength;
import org.mariella.oxygen.remoting.http.common.InputStreamAndLengthObserver;
import org.mariella.oxygen.remoting.http.common.InputStreamPlaceholder;
import org.mariella.oxygen.remoting.http.common.RemoteCall;

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
		protected final RemoteCall call;
		
		private Set<Object> objects = new HashSet<>();
		private Map<String, ObjectStatistic> typeStatistic = new HashMap<>();

		public ReplacingObjectOutputStream(OutputStream out, RemoteCall call) throws IOException {
			super(out);
			this.call = call;
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
	
	private final ClassResolver classResolver;
	private List<SendAndReceiveListener> listeners = new ArrayList<>();
	private volatile boolean cancelRequest = false;
	private int maxTries = 1;
	private long waitForNextRetry = -1L;

public SendAndReceive(ClassResolver classResolver) {
	this.classResolver = classResolver;
	if (HttpRemoting.getInstance().getSendAndReceiveInitializer() != null) {
		HttpRemoting.getInstance().getSendAndReceiveInitializer().initialize(this);
	}
}

protected void preparePost(RemoteCall<?> call, HttpPost httpPost) {
}

protected boolean isInputStreamResult() {
	return false;
}

public SendAndReceiveResult sendAndReceive(HttpClient httpclient, RemoteCall call, Invoker invoker) throws SendAndReceiveException, IOException, ClassNotFoundException, InterruptedException {
	
	HttpRemoting.getInstance().addCurrentSendAndReceive(this);
	HttpPost httpPost = null;
	try {
		
		URL url = new URL(call.getUrl());
		httpPost = new HttpPost(url.toExternalForm());
		HttpEntity requestBody = buildRequestBody(call);
		
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
		
		preparePost(call, httpPost);

		HttpResponse response = executeRetry(httpclient, httpPost, call);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new SendAndReceiveException(response.getStatusLine().getStatusCode(), response.getStatusLine().toString());
		}
		
		HttpEntity httpEntity = response.getEntity();
		Object result = readResult(httpEntity.getContent());
		
		// consume the connection immediately if content stream is not needed
		InputStream content;
		if(call instanceof RemoteCommandCall && ((RemoteCommandCall) call).getCommand() instanceof InputStreamProvidingCommand) {
			content = new HttpInputStreamWrapper(httpPost, httpEntity);
		} else {
			content = httpEntity.getContent();
			EntityUtils.consume(httpEntity); // put's the connection back to the pool
		}
		return new SendAndReceiveResult(result, content);
		
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

private HttpResponse executeRetry(HttpClient httpclient, HttpPost httppost, RemoteCall call) throws InterruptedException, IOException {
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
			if (!call.isTryReconnect())
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
	if (call.isTryReconnect() && maxTries > 1) {
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

private Object readResult(InputStream content) throws IOException, ClassNotFoundException {
	ObjectInputStream ois = new ObjectInputStream(content) {
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			return classResolver.resolveClass(desc.getName());
		}
	};
	Object result = ois.readObject();
	return result;
}

private HttpEntity buildRequestBody(RemoteCall call) throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ReplacingObjectOutputStream oos = new ReplacingObjectOutputStream(out, call);
	oos.writeObject(call);
	oos.close();
	byte[] callBytes = out.toByteArray();
	if (call instanceof RemoteCommandCall && ((RemoteCommandCall)call).getCommand() instanceof InputStreamProcessingCommand) {
		InputStreamProcessingCommand cmd = (InputStreamProcessingCommand)((RemoteCommandCall)call).getCommand();
		InputStream in1 = new ByteArrayInputStream(callBytes);
		InputStreamAndLength abortable = cmd.getProcessedInputStream();
		InputStream in2 = abortable.getInputStream();
		final AbortableInputStreamEntity entity = new AbortableInputStreamEntity(new SequenceInputStream(in1, in2), cmd.getProcessedInputStream().getLength() + callBytes.length);
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
