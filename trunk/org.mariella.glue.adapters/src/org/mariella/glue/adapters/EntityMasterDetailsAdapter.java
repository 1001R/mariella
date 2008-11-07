package org.mariella.glue.adapters;

import java.util.Collection;

import org.mariella.glue.service.Entity;
import org.mariella.rcp.adapters.MasterDetailsAdapter;


public abstract class EntityMasterDetailsAdapter<E extends Entity> extends MasterDetailsAdapter<E> {

	EntityMasterDetailsAdapterContext<E> context;
	Collection<E> entities;
	
public EntityMasterDetailsAdapter(EntityMasterDetailsAdapterContext<E> context) {
	super(context);
	this.context = context;
	refresh();
}

private void refresh() {
	entities = readEntities();
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

}
