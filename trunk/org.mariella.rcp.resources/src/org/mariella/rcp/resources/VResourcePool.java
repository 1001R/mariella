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

void fireResourceChanged(Object source, VResource resource) {
	if (resource.getRef() == null)
		// currently we only support resource change support for resources that have been persisted
		return;
	VResourceChangeEvent ev = new VResourceChangeEvent(source, resource);
	for (VResourceChangeListener l : resourceChangeListeners)
		l.resourceChanged(ev);
}

void fireResourceRemovedFromPool(Object source, VResource resource) {
	VResourceChangeEvent ev = new VResourceChangeEvent(source, resource);
	for (VResourceChangeListener l : resourceChangeListeners)
		l.resourceRemovedFromPool(ev);
}

void fireResourceLoaded(Object source, VResource resource) {
	VResourceChangeEvent ev = new VResourceChangeEvent(source, resource);
	for (VResourceChangeListener l : resourceChangeListeners)
		l.resourceLoaded(ev);
}

public void close() {}


}
