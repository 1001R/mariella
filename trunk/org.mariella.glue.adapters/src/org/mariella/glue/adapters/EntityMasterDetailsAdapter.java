package org.mariella.glue.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mariella.glue.service.Entity;
import org.mariella.rcp.adapters.MasterDetailsAdapter;


public abstract class EntityMasterDetailsAdapter<E extends Entity> extends MasterDetailsAdapter<E> {

	EntityMasterDetailsAdapterContext<E> context;
	private List<E> deletedEntities = new ArrayList<E>();
	private List<E> newEntities = new ArrayList<E>();
	

public EntityMasterDetailsAdapter(EntityMasterDetailsAdapterContext<E> context) {
	super(context);
	this.context = context;
	reload();
}

public void reload() {
	getEntityMasterDetailsAdapterContext().resetGlueContext();
	deletedEntities.clear();
	newEntities.clear();
	clearDetailsList();
	Collection<E> entities = readEntities();
	setDetails(entities);
	if (getDetailsList().size() > 0)
		setSelectedDetails(getDetailsList().get(0));
}

protected abstract  Collection<E> readEntities();

public EntityMasterDetailsAdapterContext<E> getEntityMasterDetailsAdapterContext() {
	return context;
}

public void dispose() {}


protected void createdDetails(E details) {
	newEntities.add(details);
}

public void removeDetails(E details) {
	if (newEntities.contains(details)) {
		newEntities.remove(details);
		super.removeDetails(details);
	} else {
		deletedEntities.add(details);
	}
}

public boolean save() {
	if (doSave(getDetailsList(), deletedEntities)) {
		reload();
		return true;
	}
	return false;
}

protected abstract boolean doSave(Collection<E> entities, Collection<E> deletedEntities);

public boolean isNewEntity(E entity) {
	return newEntities.contains(entity);
}

}
