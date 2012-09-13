package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingSelection;

public class GlobalClipboardActionsHandler implements ISelectionChangedListener {
abstract class GlobalClipboardAction extends Action {

GlobalClipboardAction() {
	setEnabled(false);
}
@Override
public final void run() {
	ISelection selection = bindingContext.getSelectionProvider().getSelection();
	ClipboardSupportingObservable observable = getClipboardSupportingObservable(selection);
	if (observable != null)
		implementAction(observable);
}
abstract void implementAction(ClipboardSupportingObservable observable);
}

class CutAction extends GlobalClipboardAction {
@Override
void implementAction(ClipboardSupportingObservable observable) {
	observable.doCut();
}
}
	
class CopyAction extends GlobalClipboardAction {
@Override
void implementAction(ClipboardSupportingObservable observable) {
	observable.doCopy();
}
}

class PasteAction extends GlobalClipboardAction {
@Override
void implementAction(ClipboardSupportingObservable observable) {
	observable.doPaste();
}
}

class DeleteAction extends GlobalClipboardAction {
@Override
void implementAction(ClipboardSupportingObservable observable) {
	observable.doDelete();
}
}


VBindingContext bindingContext;
CutAction cutAction;
CopyAction copyAction;
PasteAction pasteAction;
DeleteAction deleteAction;

public GlobalClipboardActionsHandler(VBindingContext bindingContext) {
	this.bindingContext = bindingContext;
	cutAction = new CutAction();
	copyAction = new CopyAction();
	pasteAction = new PasteAction();
	deleteAction = new DeleteAction();
	bindingContext.getSelectionProvider().addSelectionChangedListener(this);
}

public void dispose() {
	bindingContext.getSelectionProvider().removeSelectionChangedListener(this);
}

public void selectionChanged(SelectionChangedEvent event) {
	validateActions();
}

public void validateActions() {
	ISelection selection = bindingContext.getSelectionProvider().getSelection();
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
	if (selection instanceof VBindingSelection) {
		VTargetObservable target = ((VBindingSelection)selection).getTargetObservable();
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
	validateActions();
}
	
}