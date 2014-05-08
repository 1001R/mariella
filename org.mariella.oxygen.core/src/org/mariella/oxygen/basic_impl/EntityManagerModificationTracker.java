package org.mariella.oxygen.basic_impl;


import java.beans.PropertyChangeEvent;

import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.persistence.runtime.AbstractModificationTrackerImpl;
import org.mariella.persistence.schema.SchemaDescription;

public class EntityManagerModificationTracker extends AbstractModificationTrackerImpl {
	private static final long serialVersionUID = 1L;

	private transient OxyEntityManager entityManager;
	private String info;
	
	private transient Thread thread;

public EntityManagerModificationTracker() {
	super();
	thread = Thread.currentThread();
}
	
public void setThread(Thread thread) {
	this.thread = thread;
}

public OxyEntityManager getEntityManager() {
	return entityManager;
}
	
public void setEntityManager(OxyEntityManager entityManager) {
	this.entityManager = entityManager;
}

@Override
public SchemaDescription getSchemaDescription() {
	return entityManager.getSchemaDescription();
}

public String getInfo() {
	return info;
}

public void setInfo(String info) {
	this.info = info;
}

@Override
public void propertyChange(PropertyChangeEvent event) {
	if (thread == null)
		throw new IllegalStateException();
	if (thread != Thread.currentThread())
		throw new RuntimeException("ILLEGAL THREAD ACESS");
	super.propertyChange(event);
}

@Override
public String toString() {
	return super.toString() + " (" + info + ")";
}

}
