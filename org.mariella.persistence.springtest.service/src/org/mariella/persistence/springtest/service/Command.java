package org.mariella.persistence.springtest.service;

import java.io.Serializable;

import org.mariella.oxygen.basic_core.OxyObjectPool;

public class Command implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private OxyObjectPool objectPool;
	
public Command(OxyObjectPool objectPool) {
	super();
	this.objectPool = objectPool;
}

public OxyObjectPool getObjectPool() {
	return objectPool;
}

public void setObjectPool(OxyObjectPool objectPool) {
	this.objectPool = objectPool;
}

}
