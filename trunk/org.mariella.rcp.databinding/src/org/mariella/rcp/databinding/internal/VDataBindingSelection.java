package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.viewers.StructuredSelection;
import org.mariella.rcp.databinding.SelectionPath;

public class VDataBindingSelection extends StructuredSelection {

VTargetObservable targetObservable;	
	
public VDataBindingSelection(VTargetObservable targetObservable, SelectionPath ... pathes) {
	super(toObjectArray(pathes));
	this.targetObservable = targetObservable;
}

static Object[] toObjectArray(SelectionPath[] pathes) {
	Object[] objArray = new Object[pathes.length];
	for (int i=0; i<pathes.length; i++)
		objArray[i] = pathes[i];
	return objArray;
}

public VTargetObservable getTargetObservable() {
	return targetObservable;
}

}
