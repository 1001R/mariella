package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.VDataBindingSelectionDispatchContext;

public abstract class CustomSelectionManagementExtension extends ContextSelectionManagementExtension {

public final VDataBindingSelection completeSelectionPath(VDataBindingSelection selection) {
	Object selectionPathToken = readSelection();
	Object[] elements = selection.toArray();
	SelectionPath[] newPathes = new SelectionPath[elements.length];
	for (int i=0; i<elements.length; i++) {
		SelectionPath path = (SelectionPath)elements[i];
		newPathes[i] = new SelectionPath(new Object[]{selectionPathToken},path.qualifiers);
	}
	return new VDataBindingSelection(selection.getTargetObservable(), selection.origin, newPathes);
}

public final void dispatchSelection(VDataBindingSelectionDispatchContext dispatchCtx) {
	dispatchSelection(dispatchCtx.nextPathToken());
	dispatchCtx.invokeNextDispatcher(false);
}

protected abstract void dispatchSelection(Object selectionPathToken);

protected abstract Object readSelection();

}
