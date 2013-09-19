package org.mariella.oxygen.remoting.http.client;

import org.apache.http.client.methods.HttpPost;

public interface SendAndReceiveCallback {
	
	void preparePost(HttpPost post);

}
