package org.mariella.rcp.resources;

import java.util.EventObject;

public class VResourceSelectionEvent extends EventObject {
private static final long serialVersionUID = 1L;

VResourceSelection selection;

public VResourceSelectionEvent(Object source, VResourceSelection selection) {
	super(source);
	this.selection = selection;
}

public VResourceSelection getSelection() {
	return selection;
}

}
