package org.mariella.oxygen.basic_core;

public interface CustomMergable {

	void mergeWithSource(Object sourceEntity, CustomMergeContext ctx);

}
