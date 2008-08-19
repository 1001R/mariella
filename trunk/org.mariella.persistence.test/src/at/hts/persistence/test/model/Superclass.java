package at.hts.persistence.test.model;

import java.beans.PropertyChangeSupport;

import at.hts.persistence.runtime.Modifiable;

public class Superclass implements Modifiable {
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private Long id = null;

public PropertyChangeSupport getPropertyChangeSupport() {
	return propertyChangeSupport;
}

public Long getId() {
	return id;
}

public void setId(Long id) {
	Object oldValue = id;
	this.id = id;
	propertyChangeSupport.firePropertyChange("id", oldValue, id);
}

public Object getIdentity() {
	return id;
}

}
