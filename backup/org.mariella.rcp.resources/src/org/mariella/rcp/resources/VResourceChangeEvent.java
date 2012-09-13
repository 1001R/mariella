package org.mariella.rcp.resources;

import java.util.EventObject;

public class VResourceChangeEvent extends EventObject {
private static final long serialVersionUID = 1L;

private boolean persistentChange;
private VResource resource;

public VResourceChangeEvent(Object source, VResource resource, boolean persistentChange) {
	super(source);
	this.resource = resource;
	this.persistentChange = persistentChange;
}

public VResource getResource() {
	return resource;
}

public boolean isPersistentChange() {
	return persistentChange;
}

}
