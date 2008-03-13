package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.databinding.SelectionPath;

public class SelectionDispatchingObservableSupport implements VDataBindingSelectionDispatcher{

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

public void dispatchSelection(VDataBindingSelectionDispatchContext dispatchCtx) {
	dispatchCtx.markOffset();
	try {
		dispatchCtx.nextPathToken();
		if (dispatchCtx.matchesPath(basePath)) {
			selectionDecorator.decorateSelected();
			dispatchCtx.dispatched = true;
		}
	} finally {
		dispatchCtx.resetOffset();
	}
	if (!dispatchCtx.dispatched) 
		dispatchCtx.invokeNextDispatcher(false);
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
