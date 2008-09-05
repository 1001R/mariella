package org.mariella.rcp.adapters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractAdapter implements Adapter {

private final PropertyChangeSupport propertyChangeSupport = new FixedPropertyChangeSupport(this);
protected Adapter parent = null;
protected final AdapterContext adapterContext;
protected boolean silent = false;	// if silent, no property changes and dirty notifications are sent

public AbstractAdapter(AdapterContext context) {
	this.adapterContext = context;
}

public AbstractAdapter(AdapterContext context, Adapter parent) {
	this.adapterContext = context;
	this.parent = parent;
}

public AbstractAdapter() {
	this.adapterContext = new DefaultAdapterContext(null);
}

public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
}

public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
}

public AdapterContext getAdapterContext() {
	return adapterContext;
}

public Adapter getParent() {
	return parent;
}

public void setParent(AbstractAdapter parent) {
	this.parent = parent;
}

public void fireAdapterDirty() {
	if (silent) return;
	adapterContext.adapterDirtyNotification(this);
}

public void firePropertyChange(PropertyChangeEvent evt) {
	if (silent) return;
	propertyChangeSupport.firePropertyChange(evt);
}

public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	if (silent) return;
	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
}

public void firePropertyChange(String propertyName, int oldValue, int newValue) {
	if (silent) return;
	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
}

public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	if (silent) return;
	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
}

}
