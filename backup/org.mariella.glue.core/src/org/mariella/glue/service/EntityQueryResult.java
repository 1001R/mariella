package org.mariella.glue.service;

public abstract class EntityQueryResult {
	private long id;

public abstract Class<?> getEntityClass();
	
public long getId() {
	return id;
}

public void setId(long id) {
	this.id = id;
}

}
