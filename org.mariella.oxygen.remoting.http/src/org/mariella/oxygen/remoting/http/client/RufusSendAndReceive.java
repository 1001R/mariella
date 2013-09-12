package org.mariella.oxygen.remoting.http.client;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.remoting.http.common.RemoteCall;

public class RufusSendAndReceive extends SendAndReceive {

public RufusSendAndReceive(ClassResolver classResolver) {
	super(classResolver);
}

@Override
protected void preparePost(RemoteCall call, HttpPost httpPost) {
	Credentials creds = call.getCredentials();
	if (creds instanceof UsernamePasswordCredentials) {
		UsernamePasswordCredentials upcreds = (UsernamePasswordCredentials)creds;
		httpPost.setHeader("username", upcreds.getUsername());
		httpPost.setHeader("password", upcreds.getPassword());
	} else if (creds instanceof RepositoryTicketCredentials) {
		RepositoryTicketCredentials tcreds = (RepositoryTicketCredentials)creds;
		httpPost.setHeader("ticketId", tcreds.getTicketId().toString());
		httpPost.setHeader("username", tcreds.getUsername());
	}
}
	
}
