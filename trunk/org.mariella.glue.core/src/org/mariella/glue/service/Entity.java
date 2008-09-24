package org.mariella.glue.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import at.hts.persistence.runtime.Modifiable;

@MappedSuperclass
public class Entity implements Modifiable {
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private Long id;
	
public Entity() {
	super();
}

@Transient
public PropertyChangeSupport getPropertyChangeSupport() {
	return propertyChangeSupport;
}

public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
}

public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
}

@Transient
public Long getIdentity() {
	return id;
}

@Column(name="ID")
public Long getId() {
	return id;
}

public void setId(Long id) {
	Long oldId = this.id;
	this.id = id;
	propertyChangeSupport.firePropertyChange("id", oldId, id);
}

}
