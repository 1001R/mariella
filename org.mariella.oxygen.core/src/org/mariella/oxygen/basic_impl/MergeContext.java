package org.mariella.oxygen.basic_impl;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;

public abstract class MergeContext {
	protected OxyObjectPoolImpl myPool;
	protected OxyObjectPool sourcePool;
	protected Object customContext;
	
public MergeContext(OxyObjectPoolImpl myPool, OxyObjectPool sourcePool, Object customMergeContext) {
	super();
	this.myPool = myPool;
	this.sourcePool = sourcePool;
	this.customContext = customMergeContext;
}

public OxyObjectPoolImpl getMyPool() {
	return myPool;
}
public OxyObjectPool getSourcePool() {
	return sourcePool;
}

public abstract boolean isOverwrite(EntityState myState, EntityState sourceState);
public abstract Object getMyEntity(Object sourceEntity);

public Object getCustomContext() {
	return customContext;
}

public void setCustomContext(Object customContext) {
	this.customContext = customContext;
} 
}
