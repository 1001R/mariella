package at.hts.persistence.test.model;

import java.beans.PropertyChangeSupport;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import at.hts.persistence.runtime.Modifiable;

@MappedSuperclass
public class Superclass implements Modifiable {
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private Long id = null;

public PropertyChangeSupport getPropertyChangeSupport() {
	return propertyChangeSupport;
}

@Id
@Column(name="ID")
public Long getId() {
	return id;
}

public void setId(Long id) {
	Object oldValue = id;
	this.id = id;
	propertyChangeSupport.firePropertyChange("id", oldValue, id);
}

@Transient
public Object getIdentity() {
	return id;
}

}
