package org.mariella.rcp.databinding;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.mariella.rcp.databinding.internal.VTargetObservable;

/**
 * Implements the IStructuredSelection interface to expose the
 * origin selection that caused the event. 
 * 
 * This is needed for example if a TableViewer contains some elements
 * that have to be recognized by external selection listeners.
 * 
 * @author martin
 *
 */
public class VBindingSelection implements IStructuredSelection {

VTargetObservable targetObservable;	
IStructuredSelection origin;
SelectionPath[] selectionPathes;
	
public VBindingSelection(VTargetObservable targetObservable, IStructuredSelection origin, SelectionPath ... pathes) {
	this.targetObservable = targetObservable;
	this.origin = origin;
	this.selectionPathes = pathes;
}

public VBindingSelection(SelectionPath ... pathes) {
	this.targetObservable = null;
	this.origin = null;
	this.selectionPathes = pathes;
}

public VTargetObservable getTargetObservable() {
	return targetObservable;
}

public SelectionPath getFirstSelectionPath() {
	if (selectionPathes.length == 0) return null;
	
	return selectionPathes[0];
}

public SelectionPath[] getSelectionPathes() {
	return selectionPathes;
}

public boolean isEmptySelectionPathes() {
	return selectionPathes.length == 0;
}

public Object getFirstElement() {
	return origin == null ? null : origin.getFirstElement();
}

public Iterator iterator() {
	return origin == null ? Collections.EMPTY_LIST.iterator() : origin.iterator();
}

public int size() {
	return origin == null ? 0 : origin.size();
}

public Object[] toArray() {
	return origin == null ? new Object[0] : origin.toArray();
}

public List toList() {
	return origin == null ? Collections.EMPTY_LIST : origin.toList();
}

public boolean isEmpty() {
	return origin == null ? true : origin.isEmpty();
}

public IStructuredSelection getOrigin() {
	return origin;
}

}
