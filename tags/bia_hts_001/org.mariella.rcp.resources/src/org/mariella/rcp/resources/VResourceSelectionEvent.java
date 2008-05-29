package org.mariella.rcp.resources;

import java.util.EventObject;

public class VResourceSelectionEvent extends EventObject {
private static final long serialVersionUID = 1L;

VResourceSelection selection;
boolean onDeactivatePart;

public VResourceSelectionEvent(Object source, VResourceSelection selection, boolean onDeactivatePart) {
	super(source);
	this.selection = selection;
	this.onDeactivatePart = onDeactivatePart;
}

public VResourceSelection getSelection() {
	return selection;
}

public boolean isOnDeactivatePart() {
	return onDeactivatePart;
}

}
