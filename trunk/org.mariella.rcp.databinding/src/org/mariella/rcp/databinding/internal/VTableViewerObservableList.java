package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.mariella.rcp.databinding.TableViewerCheckboxExtension;
import org.mariella.rcp.databinding.VBindingContext;

import sun.security.action.GetBooleanAction;

public class VTableViewerObservableList extends VStructuredViewerObservableList implements EnabledObservableValueFactory {

	TableViewerCheckboxExtension checkboxExtension = null;
	
public VTableViewerObservableList(VBindingContext bindingContext, TableViewer tableViewer, Class elementType) {
	super(bindingContext, tableViewer, elementType);
}

@Override
void completeDispatchSelection(int index, VDataBindingSelectionDispatchContext dispatchCtx) {
	((TableController)controller).dispatchSelection(index, dispatchCtx);
}

public TableViewer getTableViewer() {
	return (TableViewer)getViewer();
}

@Override
protected Object implementGetElementAt(int index) {
	return ((TableViewer)viewer).getElementAt(index);
}

@Override
protected void implementInsert(Object element, int index) {
	((TableViewer)viewer).insert(element, index);
}

@Override
protected void implementRemove(Object element) {
	((TableViewer)viewer).remove(element);
}

@Override
protected void implementSetTopIndex(int index) {
	((TableViewer)viewer).getTable().setTopIndex(index);
}

public EnabledObservableValue createEnabledObservableValue() {
	return new VTableViewerEnabledObservableValue(this);
}

@Override
public void aboutToDispose() {}

@Override
int getIndexInControl(Object element) {
	int index = 0;
	for (TableItem item : ((TableViewer)viewer).getTable().getItems()) {
		if (item.getData() == element)
			return index;
		index++;
	}
	return -1;
}

public void install(TableViewerCheckboxExtension tableViewerCheckboxExtension) {
	this.checkboxExtension = tableViewerCheckboxExtension;
	
	((CheckboxTableViewer)viewer).addCheckStateListener(new ICheckStateListener() {
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			PropertyPathSupport pathSupp = new PropertyPathSupport();
			pathSupp.propertyPath = checkboxExtension.getPropertyPath();
			pathSupp.object = event.getElement();
			pathSupp.initialize();
			pathSupp.implementDoSetValue(event.getChecked());
		}
	});
}

@Override
protected void elementPropertyChange(PropertyChangeEvent ev) {
	if (checkboxExtension == null) return;
	
	PropertyPathSupport pathSupp = new PropertyPathSupport();
	pathSupp.propertyPath = checkboxExtension.getPropertyPath();
	pathSupp.object = ev.getSource();
	pathSupp.initialize();
	boolean selected = (Boolean)pathSupp.implementDoGetValue();
	((CheckboxTableViewer)viewer).setChecked(pathSupp.readTargetObject(), selected);
}

}
