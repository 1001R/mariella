package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.mariella.rcp.databinding.SelectionPath;

public abstract class VAbstractButtonObservableValue extends AbstractSWTObservableValue implements SelectionAwareObservable, EnabledObservableValueFactory, VTargetObservable {

final Button button;
private Object selectionValue;
private final SelectionDispatchingObservableSupport selectionDispatchingSupport;

private Listener updateListener = new Listener() {
	public void handleEvent(Event event) {
		Object oldSelectionValue = selectionValue;
		selectionValue = doGetValue();
		fireValueChange(Diffs.createValueDiff(oldSelectionValue, selectionValue));
		event.doit = false;
	}
};

public VAbstractButtonObservableValue(Button button) {
	super(button);
	this.button = button;
	selectionDispatchingSupport = new SelectionDispatchingObservableSupport(this, new ControlSelectionDecorator(button)); 
	button.addListener(SWT.Selection, updateListener);
	button.addListener(SWT.DefaultSelection, updateListener);
}

public boolean isResponsibleFor(Control control) {
	return control == button;
}

public void doSetValue(final Object value) {
	Object oldSelectionValue = selectionValue;
	selectionValue = value;
	button.setSelection(isSelectedValue(value));
	fireValueChange(Diffs.createValueDiff(oldSelectionValue, selectionValue));
}

protected abstract boolean isSelectedValue(Object value);

public synchronized void dispose() {
	super.dispose();

	if (!button.isDisposed()) {
		button.removeListener(SWT.Selection, updateListener);
		button.removeListener(SWT.DefaultSelection, updateListener);
	}
}

public boolean dispatchSelectionPath(SelectionPath path, int offset) {
	return selectionDispatchingSupport.implementDispatchSelectionPath(path, offset);
}

public void setSelectionBasePath(Object[] path) {
	selectionDispatchingSupport.implementSetSelectionBasePath(path);
}

public VDataBindingSelection getSelection() {
	if (button != null && !button.isDisposed() && button.isFocusControl())
		return selectionDispatchingSupport.implementGetSelection();
	return null;
}

public EnabledObservableValue createEnabledObservableValue() {
	return new DefaultEnabledObservableValue(button);
}

public void setSelectionTargetControl(Control control) {
	selectionDispatchingSupport.setSelectionTargetControl(control);
}

public void extensionsInstalled() {
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	selectionDispatchingSupport.setContextSelectionCallback(getContextSelectionCallback);
}

public boolean blockDefaultTraversing() {
	return false;
}

}

