package org.mariella.glue.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mariella.glue.service.Entity;
import org.mariella.rcp.adapters.MasterDetailsAdapter;


public abstract class EntityMasterDetailsAdapter<E extends Entity> extends MasterDetailsAdapter<E> {

	EntityMasterDetailsAdapterContext<E> context;
	private List<E> deletedEntities = new ArrayList<E>();

public EntityMasterDetailsAdapter(EntityMasterDetailsAdapterContext<E> context) {
	super(context);
	this.context = context;
	reload();
}

public void reload() {
	deletedEntities.clear();
	clearDetailsList();
	Collection<E> entities = readEntities();
	for (E entity : entities) {
		addDetails(entity);
	}
}

protected abstract  Collection<E> readEntities();

public EntityMasterDetailsAdapterContext<E> getEntityMasterDetailsAdapterContext() {
	return context;
}

public void dispose() {}

protected void removedDetails(E details) {
	deletedEntities.add(details);
}

public void save() throws Exception {
	doSave(getDetailsList(), deletedEntities);
	reload();
}

protected abstract void doSave(Collection<E> entities, Collection<E> deletedEntities) throws Exception;

}
