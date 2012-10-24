package org.mariella.oxygen.basic_impl;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;

public abstract class MergeContext {
	protected OxyObjectPoolImpl myPool;
	protected OxyObjectPool sourcePool;
	
public MergeContext(OxyObjectPoolImpl myPool, OxyObjectPool sourcePool) {
	super();
	this.myPool = myPool;
	this.sourcePool = sourcePool;
}

public OxyObjectPoolImpl getMyPool() {
	return myPool;
}
public OxyObjectPool getSourcePool() {
	return sourcePool;
}

public abstract boolean isOverwrite(EntityState myState, EntityState sourceState);
public abstract Object getMyEntity(Object sourceEntity); 
}
