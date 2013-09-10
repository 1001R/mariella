package org.mariella.oxygen.basic_core;


public interface CustomMergeContext {
	OxyObjectPool getMyPool();
	OxyObjectPool getSourcePool();
	Object getCustomContext();

}
