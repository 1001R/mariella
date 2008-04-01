package org.mariella.sample.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class Entity {

private static int lastId = 0;
private Integer id;
PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

public Entity() {
	id = ++lastId;
}

public void addPropertyChangeListener(PropertyChangeListener l) {
	propertyChangeSupport.addPropertyChangeListener(l);
}

public void removePropertyChangeListener(PropertyChangeListener l) {
	propertyChangeSupport.removePropertyChangeListener(l);
}

public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

public boolean equals(Object obj) {
	if (!(obj instanceof Entity)) return false;
	return id.equals(((Entity)obj).getId());
}

public int hashCode() {
	return id.hashCode();
}

}
