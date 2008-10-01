package org.mariella.glue.service;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import at.hts.persistence.loader.ClusterLoader;
import at.hts.persistence.loader.ClusterLoaderConditionProvider;
import at.hts.persistence.loader.LoaderContext;
import at.hts.persistence.mapping.SchemaMapping;
import at.hts.persistence.persistor.ClusterDescription;
import at.hts.persistence.persistor.Persistor;
import at.hts.persistence.runtime.Modifiable;
import at.hts.persistence.schema.ClassDescription;

public abstract class ModifiableServiceImpl <T extends Modifiable> extends ScreeningServiceImpl implements ModifiableService<T> {

@SuppressWarnings("unchecked")
protected abstract Class getEntityClass();
protected abstract SchemaMapping getSchemaMapping();

protected void assertNotReadonly(Context context) {
}

public Collection<T> getAllEntities(Context context) {
	throw new UnsupportedOperationException();
}

@SuppressWarnings("unchecked")
public T create(Context context) {
	assertNotReadonly(context);
	
	try {
		T instance = (T)getEntityClass().newInstance();
		context.getModificationTracker().addNewParticipant(instance);
		return instance;
	} catch(Exception e) {
		throw new RuntimeException(e);
	}
}

public void save(T entity, Context context) {
	assertNotReadonly(context);
	saveChanges(context);
}

public void saveChanges(final Context context) {
	assertNotReadonly(context);
	new TransactionalRunner(jdbcTemplate.getDataSource()).run(
			new TransactionalRunnable<T>() {
				public T run(TransactionalRunner tr) {
					Persistor persistor = context.createPersistor(tr.getConnection());
					persistor.persist();
					return null;
				}
			}
		);
}

protected abstract ClusterDescription getEditingClusterDescription();

protected ClusterDescription getDeleteClusterDescription() {
	return getEditingClusterDescription();
}

public ClassDescription getClassDescription() {
	return getSchemaMapping().getSchemaDescription().getClassDescription(getEntityClass().getName());
}

public T loadForEditing(final Context context, Object identity, boolean isUpdate) {
	return load(context, getEditingClusterDescription(), identity, isUpdate);
}

public T loadForDelete(final Context context, Object identity, boolean isUpdate) {
	return load(context, getDeleteClusterDescription(), identity, isUpdate);
}

public T loadFlat(final Context context, Object identity, boolean isUpdate) {
	return load(context, new ClusterDescription(getClassDescription(), "root"), identity, isUpdate);
}

public T load(final Context context, final ClusterDescription cd, final Object identity, final boolean isUpdate) {
	return new TransactionalRunner(jdbcTemplate.getDataSource()).run(
			new TransactionalRunnable<T>() {
				@SuppressWarnings("unchecked")
				public T run(TransactionalRunner tr) {
					ClusterLoader clusterLoader = context.createClusterLoader(cd);
					LoaderContext loaderContext = context.createLoaderContext();
					loaderContext.setUpdate(isUpdate);
					return (T)clusterLoader.load(tr.getConnection(), loaderContext, identity);
				}
			}
		);
}

@SuppressWarnings("unchecked")
public T load(Context context, Connection connection, ClusterDescription cd, Object identity, boolean isUpdate) {
	ClusterLoader clusterLoader = context.createClusterLoader(cd);
	LoaderContext loaderContext = context.createLoaderContext();
	loaderContext.setUpdate(isUpdate);
	return (T)clusterLoader.load(connection, loaderContext, identity);
}

public List<T> load(final Context context, final ClusterDescription cd, final boolean isUpdate, final ClusterLoaderConditionProvider conditionProvider) {
	return new TransactionalRunner(jdbcTemplate.getDataSource()).run(
			new TransactionalRunnable<List<T>>() {
				@SuppressWarnings("unchecked")
				public List<T> run(TransactionalRunner tr) {
					ClusterLoader clusterLoader = context.createClusterLoader(cd);
					LoaderContext loaderContext = context.createLoaderContext();
					loaderContext.setUpdate(isUpdate);
					return (List<T>)clusterLoader.load(tr.getConnection(), loaderContext, conditionProvider);
				}
			}
		);
}

public void delete(Context context, T entity) {
	assertNotReadonly(context);
	doDelete(context, entity);
	saveChanges(context);
}

public void doDelete(Context context, T entity) {
	assertNotReadonly(context);
	context.getModificationTracker().remove(entity);
}
	
}
