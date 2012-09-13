package org.mariella.glue.service;

public interface EntityServiceListener {

public void entityAdded(EntityServiceImpl<?> service, Entity entity);
public void entityRemoved(EntityServiceImpl<?> service, Entity entity);
public void entitySaved(EntityServiceImpl<?> service, Entity entity);
	
}
