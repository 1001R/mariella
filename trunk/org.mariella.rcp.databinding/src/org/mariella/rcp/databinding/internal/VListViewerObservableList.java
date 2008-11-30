package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.mariella.rcp.databinding.VBindingContext;

public class VListViewerObservableList extends VStructuredViewerObservableList {

public VListViewerObservableList(VBindingContext bindingContext, AbstractListViewer structuredViewer, Class elementType) {
	super(bindingContext, structuredViewer, elementType);
}

@Override
protected Object implementGetElementAt(int index) {
	return ((AbstractListViewer)viewer).getElementAt(index);
}

@Override
protected void implementInsert(Object element, int index) {
	((AbstractListViewer)viewer).insert(element, index);
}

@Override
protected void implementRemove(Object element) {
	((AbstractListViewer)viewer).remove(element);
}

@Override
protected void implementSetTopIndex(int index) {}

public AbstractListViewer getListViewer() {
	return (AbstractListViewer)getViewer();
}

@Override
int getIndexInControl(Object element) {
	return -1;
}

}
