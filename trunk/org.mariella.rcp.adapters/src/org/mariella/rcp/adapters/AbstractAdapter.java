package org.mariella.rcp.adapters;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractAdapter implements Adapter {

protected final PropertyChangeSupport propertyChangeSupport = new FixedPropertyChangeSupport(this);
protected final AdapterContext adapterContext;

public AbstractAdapter(AdapterContext context) {
	this.adapterContext = context;
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

}
