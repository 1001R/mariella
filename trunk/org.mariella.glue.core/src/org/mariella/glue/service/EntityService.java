package org.mariella.glue.service;

import java.util.Collection;

import at.hts.persistence.schema.ClassDescription;

public interface EntityService <T extends Entity> extends ModifiableService<T> {
	
public void addListener(EntityServiceListener listener);
public void removeListener(EntityServiceListener listener);
public T create(Context context);
public Collection<T> getAllEntities(Context context);
public void save(T entity, Context context);
public ClassDescription getClassDescription();
public void delete(Context context, T entity);
public void doDelete(Context context, T entity);
}
