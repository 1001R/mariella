package org.mariella.oxygen.service.server.spring;


import java.io.Serializable;

import org.mariella.oxygen.basic_core.OxyObjectPool;

public final class ServiceResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private OxyObjectPool objectPool;
	private byte[] nativeResult;

public OxyObjectPool getObjectPool() {
	return objectPool;
}

public void setObjectPool(OxyObjectPool objectPool) {
	this.objectPool = objectPool;
}

public byte[] getNativeResult() {
	return nativeResult;
}

public void setNativeResult(byte[] nativeResult) {
	this.nativeResult = nativeResult;
}

}
