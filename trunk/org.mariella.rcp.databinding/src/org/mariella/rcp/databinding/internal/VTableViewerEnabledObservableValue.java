package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.swt.widgets.Control;

public class VTableViewerEnabledObservableValue extends AbstractObservableValue implements EnabledObservableValue, VTargetObservable {

VTableViewerObservableList tableViewerObservableList;

public VTableViewerEnabledObservableValue(VTableViewerObservableList tableViewerObservableList) {
	this.tableViewerObservableList = tableViewerObservableList;
}

public boolean isResponsibleFor(Control control) {
	return control == tableViewerObservableList.getTableViewer().getControl();
}

protected Object doGetValue() {
	throw new UnsupportedOperationException();
}

protected void doSetValue(Object value) {
	tableViewerObservableList.tableController.setEditable((Boolean)value);
}

public Object getValueType() {
	return Boolean.class;
}

public void extensionsInstalled() {
}

public boolean blockDefaultTraversing() {
	return false;
}

}
