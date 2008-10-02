package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.VBindingContext;

public class VTableViewerObservableList extends VStructuredViewerObservableList implements EnabledObservableValueFactory {

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
public void aboutToDispose() {
	// TODO Auto-generated method stub
	
}

}
