package org.mariella.rcp.adapters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

/**
 * Fixes the problem that an event is fired when oldValue and the 
 * newValue is null.
 * 
 * 
 * @author maschmid
 *
 */
public class FixedPropertyChangeSupport extends PropertyChangeSupport {

	private static final long serialVersionUID = 1L;

public FixedPropertyChangeSupport(Object sourceBean) {
	super(sourceBean);
}


@Override
public void firePropertyChange(PropertyChangeEvent evt) {
	if (evt.getOldValue() == null && evt.getNewValue() == null) return;
	
	super.firePropertyChange(evt);
}

}
