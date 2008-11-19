package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;

public class EnabledOnSelectedCallback implements EnabledCallback2, ISelectionChangedListener {

ISelectionProvider selectionProvider;
EnabledStateModelObservableValue enabledStateValue;
boolean hadSelectionChangedEvent = false;

public EnabledOnSelectedCallback(ISelectionProvider selectionProvider) {
	this.selectionProvider = selectionProvider;
}

public boolean isEnabled() {
	return ((IStructuredSelection)selectionProvider.getSelection()).size() > 0;
}

public void dispose() {
	selectionProvider.removeSelectionChangedListener(this);
}

public void selectionChanged(SelectionChangedEvent event) {
	hadSelectionChangedEvent = true;
	enabledStateValue.revalidate();
}

public void install(EnabledStateModelObservableValue value) {
	enabledStateValue = value;
	selectionProvider.addSelectionChangedListener(this);
}

public void uninstall(EnabledStateModelObservableValue value) {
	selectionProvider.removeSelectionChangedListener(this);
}


}
