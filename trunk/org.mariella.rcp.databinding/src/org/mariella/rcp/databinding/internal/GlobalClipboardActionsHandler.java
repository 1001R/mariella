package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.mariella.rcp.databinding.VDataBindingContext;

public class GlobalClipboardActionsHandler implements ISelectionChangedListener {
abstract class GlobalClipboardAction extends Action {

GlobalClipboardAction() {
	setEnabled(true);
}
public final void run() {
	ISelection selection = dataBindingContext.getSelectionProvider().getSelection();
	ClipboardSupportingObservable observable = getClipboardSupportingObservable(selection);
	if (observable != null)
		implementAction(observable);
}
abstract void implementAction(ClipboardSupportingObservable observable);
}

class CutAction extends GlobalClipboardAction {
void implementAction(ClipboardSupportingObservable observable) {
	observable.doCut();
}
}
	
class CopyAction extends GlobalClipboardAction {
void implementAction(ClipboardSupportingObservable observable) {
	observable.doCopy();
}
}

class PasteAction extends GlobalClipboardAction {
void implementAction(ClipboardSupportingObservable observable) {
	observable.doPaste();
}
}

class DeleteAction extends GlobalClipboardAction {
void implementAction(ClipboardSupportingObservable observable) {
	observable.doDelete();
}
}


VDataBindingContext dataBindingContext;
CutAction cutAction;
CopyAction copyAction;
PasteAction pasteAction;
DeleteAction deleteAction;

public GlobalClipboardActionsHandler(VDataBindingContext dataBindingContext) {
	this.dataBindingContext = dataBindingContext;
	cutAction = new CutAction();
	copyAction = new CopyAction();
	pasteAction = new PasteAction();
	deleteAction = new DeleteAction();
	dataBindingContext.getSelectionProvider().addSelectionChangedListener(this);
}

public void dispose() {
	dataBindingContext.getSelectionProvider().removeSelectionChangedListener(this);
}

public void selectionChanged(SelectionChangedEvent event) {
	validateActions();
}

public void validateActions() {
	ISelection selection = dataBindingContext.getSelectionProvider().getSelection();
	ClipboardSupportingObservable observable = getClipboardSupportingObservable(selection);
	if (observable != null) {
		cutAction.setEnabled(observable.canCut());
		copyAction.setEnabled(observable.canCopy());
		pasteAction.setEnabled(observable.canPaste());
		deleteAction.setEnabled(observable.canDelete());
	} else {
		cutAction.setEnabled(false);
		copyAction.setEnabled(false);
		pasteAction.setEnabled(false);
		deleteAction.setEnabled(false);
	}
}

private ClipboardSupportingObservable getClipboardSupportingObservable(ISelection selection) {
	if (selection instanceof VDataBindingSelection) {
		VTargetObservable target = ((VDataBindingSelection)selection).targetObservable;
		if (target instanceof ClipboardSupportingObservable) {
			return (ClipboardSupportingObservable)target;
		}
	}
	return null;
}

public void initialize(IActionBars actionBars) {
	actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
	actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
	actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
	actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
}
	
}
