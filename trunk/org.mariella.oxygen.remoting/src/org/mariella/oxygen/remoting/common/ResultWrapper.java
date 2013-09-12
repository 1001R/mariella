package org.mariella.oxygen.remoting.common;

import java.io.Serializable;

import org.mariella.oxygen.basic_core.OxyObjectPool;

public class ResultWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final OxyObjectPool pool;
	private final byte[] serializedResult; // result needs to be deserialized when ObjectPool is available
	
	public ResultWrapper(OxyObjectPool pool, byte[] serializedResult) {
		super();
		this.pool = pool;
		this.serializedResult = serializedResult;
	}

	public OxyObjectPool getPool() {
		return pool;
	}

	public byte[] getSerializedResult() {
		return serializedResult;
	}

}
