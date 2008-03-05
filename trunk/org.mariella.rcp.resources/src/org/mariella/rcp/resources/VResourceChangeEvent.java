package org.mariella.rcp.resources;

import java.util.EventObject;

public class VResourceChangeEvent extends EventObject {
private static final long serialVersionUID = 1L;

private VResource resource;

public VResourceChangeEvent(Object source, VResource resource) {
	super(source);
	this.resource = resource;
}

public VResource getResource() {
	return resource;
}

}
