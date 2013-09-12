package org.mariella.oxygen.remoting.http.common;

import java.io.Serializable;

import org.apache.http.auth.Credentials;
import org.mariella.oxygen.basic_core.OxyObjectPool;

public interface RemoteCall<T> extends Serializable {
	
public abstract String getUrl();
public boolean isTryReconnect();	
public Credentials getCredentials();

public T getCommand();
public OxyObjectPool getObjectPool();


}
