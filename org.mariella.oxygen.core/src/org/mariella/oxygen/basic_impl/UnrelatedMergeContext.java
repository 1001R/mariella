package org.mariella.oxygen.basic_impl;


import java.util.ArrayList;
import java.util.List;

import org.mariella.oxygen.basic_core.EntityState;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.persistence.schema.ClassDescription;

public class UnrelatedMergeContext extends MergeContext {
	private List<Object> overwrite = new ArrayList<Object>();
	
public UnrelatedMergeContext(OxyObjectPoolImpl myPool, OxyObjectPool sourcePool) {
	super(myPool, sourcePool);
}

@Override
public Object getMyEntity(Object sourceEntity) {
	EntityState sourceState = sourcePool.getEntityState(sourceEntity);
	EntityState myState = null;

	if(!sourceState.isNew()) {
		Object myEntity = myPool.getEntityForPersistentId(sourceState.getPersistentIdentity());
		if(myEntity != null) {
			myState = myPool.getEntityState(myEntity);
		}
	}
	if(myState == null) {
		ClassDescription cd = (ClassDescription)myPool.getEntityManager().getObjectDescription(myPool.getEntityManager().getClass(sourceState.getClassDescription()));
		Object myEntity = myPool.createEntity(cd);
		myState = myPool.createEntityState(myEntity);
		overwrite.add(myState);
		myState.merge(this, sourceState);
	}
	return myState.getEntity();
}

@Override
public boolean isOverwrite(EntityState myState, EntityState sourceState) {
	return overwrite.contains(myState);
}

}
