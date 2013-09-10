package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBindingSelection;

public class SelectionDispatchingObservableSupport implements VDataBindingSelectionDispatcher{

private SelectionDecorator selectionDecorator;
private Object[] basePath;
private GetContextSelectionCallback contextSelectionCallback;
private VTargetObservable targetObservable;
private boolean offsetSelection = false;

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
		if (!(offsetSelection && dispatchCtx.dispatched))
			dispatchCtx.resetOffset();
	}
	if (!dispatchCtx.dispatched) 
		dispatchCtx.invokeNextDispatcher(false);
}

public VBindingSelection implementGetSelection() {
	if (contextSelectionCallback == null) 
		return new VBindingSelection(targetObservable, new StructuredSelection(), new SelectionPath(basePath));
	
	VBindingSelection contextSelection = contextSelectionCallback.getContextSelection();
	if (contextSelection == null || contextSelection.getSelectionPathes().length == 0) return null;
	
	SelectionPath contextSelectionPath = contextSelection.getSelectionPathes()[0];
	return new VBindingSelection(targetObservable, contextSelection.getOrigin(), new SelectionPath(contextSelectionPath.getQualifiers(), basePath));
}

public void setSelectionTargetControl(Control control) {
	selectionDecorator.setSelectionTargetControl(control);
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	this.contextSelectionCallback = getContextSelectionCallback;
}

public void setOffsetSelection(boolean offsetSelection) {
	this.offsetSelection = offsetSelection;
}

}