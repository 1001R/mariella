package org.mariella.persistence.mapping;

public interface IBatchStrategy {

	/**
	 * @return The persistent classes to use batched update/insert.
	 * The returned classes are persisted at the end of a save-cycle in the order they are returned. 
	 */
	Object[] getOrderedBatchClasses();
	
	/**
	 * @return The maximum number of overall rows batched before executing the batched statements.
	 */
	int getMaxBatchSize();
}
