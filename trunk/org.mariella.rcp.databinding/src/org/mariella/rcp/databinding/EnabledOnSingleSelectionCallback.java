package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;

public class EnabledOnSingleSelectionCallback implements EnabledCallback, ISelectionChangedListener {

ISelectionProvider selectionProvider;
EnabledStateModelObservableValue enabledStateValue;
boolean enabled = false;

public EnabledOnSingleSelectionCallback(ISelectionProvider selectionProvider) {
	this.selectionProvider = selectionProvider;
}

public boolean isEnabled() {
	return enabled;
}

public void dispose() {
	selectionProvider.removeSelectionChangedListener(this);
}

public void selectionChanged(SelectionChangedEvent event) {
	enabled = ((IStructuredSelection)event.getSelection()).size() == 1;
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
