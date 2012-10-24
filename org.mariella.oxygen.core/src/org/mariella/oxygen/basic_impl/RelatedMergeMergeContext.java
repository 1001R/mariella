package org.mariella.oxygen.basic_impl;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;

public class RelatedMergeMergeContext extends MergeContext {
	
public RelatedMergeMergeContext(OxyObjectPoolImpl myPool, OxyObjectPool sourcePool) {
	super(myPool, sourcePool);
}

public boolean isOverwrite(EntityState myState, EntityState sourceState) {
	return sourceState.getFlushTimestamp() > myState.getFlushTimestamp();
}

public Object getMyEntity(Object sourceEntity) {
	return myPool.getEntityStateForPoolId(sourcePool.getEntityState(sourceEntity).getPoolIdentity()).getEntity();
}

}
