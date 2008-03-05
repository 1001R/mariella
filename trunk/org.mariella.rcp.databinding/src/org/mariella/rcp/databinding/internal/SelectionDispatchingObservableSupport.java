package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.databinding.SelectionPath;

public class SelectionDispatchingObservableSupport {

private SelectionDecorator selectionDecorator;
private Object[] basePath;
private GetContextSelectionCallback contextSelectionCallback;
private VTargetObservable targetObservable;

public SelectionDispatchingObservableSupport(VTargetObservable targetObservable, SelectionDecorator selectionDecorator) {
	this.selectionDecorator = selectionDecorator;
	this.targetObservable = targetObservable;
}

public void implementSetSelectionBasePath(Object[] path) {
	this.basePath = path;;
}

public boolean implementDispatchSelectionPath(SelectionPath path, int offset) {
	if (hasBasePath(path, offset)) {
		selectionDecorator.decorateSelected();
		return true;
	}
	return false;
}

private boolean hasBasePath(SelectionPath path, int offset) {
	for (int i=offset, c = 0; i<offset+basePath.length; i++, c++)
		if (!path.getQualifiers()[i].equals(basePath[c]))	return false;
	return true;
}

public VDataBindingSelection implementGetSelection() {
	if (contextSelectionCallback == null) 
		return new VDataBindingSelection(targetObservable, new SelectionPath(basePath));
	
	VDataBindingSelection contextSelection = contextSelectionCallback.getContextSelection();
	if (contextSelection == null || contextSelection.isEmpty()) return null;
	
	SelectionPath contextSelectionPath = (SelectionPath)contextSelection.getFirstElement();
	return new VDataBindingSelection(targetObservable, new SelectionPath(contextSelectionPath.getQualifiers(), basePath));
}

public void setSelectionTargetControl(Control control) {
	selectionDecorator.setSelectionTargetControl(control);
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	this.contextSelectionCallback = getContextSelectionCallback;
}

}
