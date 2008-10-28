package org.mariella.glue.service;

import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.runtime.ModificationInfo;


public abstract class EntityServiceImpl <T extends Entity> extends ModifiableServiceImpl<T> implements EntityService<T> {
	protected List<EntityServiceListener> listeners = new ArrayList<EntityServiceListener>();

public void addListener(EntityServiceListener listener) {
	listeners.add(listener);
}

public void removeListener(EntityServiceListener listener) {
	listeners.remove(listener);
}

public void save(T entity, Context context) {
	ModificationInfo mi = context.getModificationTracker().getModificationInfo(entity);
	boolean isNew = mi != null && mi.getStatus() == ModificationInfo.Status.New;
	
	super.save(entity, context);

	for(EntityServiceListener listener : listeners) {
		if(isNew) {
			listener.entityAdded(this, entity);
		} else {
			listener.entitySaved(this, entity);
		}
	}
}

public void delete(Context context, T entity) {
	super.delete(context, entity);
	for(EntityServiceListener listener : listeners) {
		listener.entityRemoved(this, entity);
	}
}

}
