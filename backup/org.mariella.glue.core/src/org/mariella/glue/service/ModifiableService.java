package org.mariella.glue.service;

import java.util.Collection;

import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.runtime.Modifiable;
import org.mariella.persistence.schema.ClassDescription;


public interface ModifiableService <T extends Modifiable> extends ScreeningService {

public T create(Context context);
public Collection<T> getAllEntities(Context context);
public void save(T entity, Context context);
public T loadForEditing(Context context, Object identity, boolean isUpdate);
public T loadForDelete(final Context context, Object identity, boolean isUpdate);
public T load(Context context, ClusterDescription cd, Object identity, boolean isUpdate);
public T loadFlat(Context context, Object identity, boolean isUpdate);
public ClassDescription getClassDescription();
public void delete(Context context, T entity);
public void doDelete(Context context, T entity);

}
