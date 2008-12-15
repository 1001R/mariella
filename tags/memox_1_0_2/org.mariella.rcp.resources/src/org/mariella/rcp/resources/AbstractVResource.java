package org.mariella.rcp.resources;



public abstract class AbstractVResource implements VResource {

VResourceRef ref;

public AbstractVResource() {
}

public VResourceRef getRef() {
	return ref;
}

public void fireChanged() {
	if (getRef() != null) 
		VResourcesPlugin.getResourcePool().fireResourceChanged(this, this, false);
}

public void fireIfChanged(Object lastValue, Object newValue) {
	if (getRef() != null && changed(lastValue, newValue))
		fireChanged();
}

@SuppressWarnings("unchecked")
private boolean changed(Object lastValue, Object newValue) {
	if (lastValue == null) 
		return newValue != null;
	if (lastValue instanceof Comparable) 
		return ((Comparable)lastValue).compareTo(newValue) != 0;
	return !lastValue.equals(newValue);
}

public void setRef(VResourceRef ref) {
	this.ref = ref;
}

public void dispose() {
}

}
