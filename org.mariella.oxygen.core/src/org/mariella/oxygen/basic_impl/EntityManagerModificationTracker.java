package org.mariella.oxygen.basic_impl;


import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.persistence.runtime.AbstractModificationTrackerImpl;
import org.mariella.persistence.schema.SchemaDescription;

public class EntityManagerModificationTracker extends AbstractModificationTrackerImpl {
	private static final long serialVersionUID = 1L;

	private transient OxyEntityManager entityManager;
	private String info;

public EntityManagerModificationTracker() {
	super();
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
public String toString() {
	return super.toString() + " (" + info + ")";
}

}
