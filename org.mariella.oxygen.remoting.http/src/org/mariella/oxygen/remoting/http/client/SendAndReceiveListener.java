package org.mariella.oxygen.remoting.http.client;

public interface SendAndReceiveListener {
	
	void connectRetryAfterFailed(SendAndReceive snr, int attempt);

	void connectFailed(SendAndReceive snr, int attempt);

	void connectCanceled(SendAndReceive snr, int attempt);

	void connectOk(SendAndReceive snr, int attempt);

	void aboutToLeave(SendAndReceive snr);

}
