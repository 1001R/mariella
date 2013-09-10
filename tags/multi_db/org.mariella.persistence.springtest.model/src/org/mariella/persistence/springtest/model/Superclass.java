package org.mariella.persistence.springtest.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Superclass implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
}

public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
}

@Id
@Column(name="ID")
public String getId() {
	return id;
}

public void setId(String id) {
	String old = this.id;
	this.id = id;
	propertyChangeSupport.firePropertyChange("id", old, id);
}
	
}
