package org.mariella.oxygen.basic_impl;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;

public class RelatedMergeMergeContext extends MergeContext {
	
public RelatedMergeMergeContext(OxyObjectPoolImpl myPool, OxyObjectPool sourcePool, Object customMergeContext) {
	super(myPool, sourcePool, customMergeContext);
}

public boolean isOverwrite(EntityState myState, EntityState sourceState) {
	return sourceState.getFlushTimestamp() > myState.getFlushTimestamp();
}

public Object getMyEntity(Object sourceEntity) {
	EntityState sourceState = sourcePool.getEntityState(sourceEntity);
	if (sourceState == null)
		throw new IllegalStateException("No entity state for entity " + sourceEntity + ". Entity does not seem to be in the object pool.");
	EntityState myState = myPool.getEntityStateForPoolId(sourceState.getPoolIdentity());
	if (myState == null)
		throw new IllegalStateException();
	
	return myState.getEntity();
}

}
