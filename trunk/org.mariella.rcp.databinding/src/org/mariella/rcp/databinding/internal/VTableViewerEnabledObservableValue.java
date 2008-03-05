package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;

public class VTableViewerEnabledObservableValue extends AbstractObservableValue implements EnabledObservableValue, VTargetObservable {

VTableViewerObservableList tableViewerObservableList;

public VTableViewerEnabledObservableValue(VTableViewerObservableList tableViewerObservableList) {
	this.tableViewerObservableList = tableViewerObservableList;
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

@Override
public void extensionsInstalled() {
	// TODO Auto-generated method stub
	
}

}
