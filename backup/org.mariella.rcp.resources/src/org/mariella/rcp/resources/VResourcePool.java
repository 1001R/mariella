package org.mariella.rcp.resources;

import java.util.ArrayList;
import java.util.List;

public class VResourcePool {

private List<VResourceChangeListener> resourceChangeListeners = new ArrayList<VResourceChangeListener>();
	
public void addResourceChangeListener(VResourceChangeListener l) {
	resourceChangeListeners.add(l);
}

public void removeResourceChangeListener(VResourceChangeListener l) {
	resourceChangeListeners.remove(l);
}

void fireResourceChanged(Object source, VResource resource, boolean persistentChange) {
	if (resource.getRef() == null)
		// currently we only support resource change support for resources that have been persisted
		return;
	VResourceChangeEvent ev = new VResourceChangeEvent(source, resource, persistentChange);
	for (VResourceChangeListener l : resourceChangeListeners)
		l.resourceChanged(ev);
}

void fireResourceRemoved(Object source, VResource resource, boolean persistentChange) {
	VResourceChangeEvent ev = new VResourceChangeEvent(source, resource, persistentChange);
	for (VResourceChangeListener l : resourceChangeListeners)
		l.resourceRemoved(ev);
}

void fireResourceLoaded(Object source, VResource resource, boolean persistentChange) {
	VResourceChangeEvent ev = new VResourceChangeEvent(source, resource, persistentChange);
	for (VResourceChangeListener l : resourceChangeListeners)
		l.resourceLoaded(ev);
}

public void close() {}


}
