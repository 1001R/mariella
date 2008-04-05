package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.list.AbstractObservableList;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingSelection;

public class VTableViewerObservableList extends AbstractObservableList implements ISWTObservable, SelectionAwareObservable, EnabledObservableValueFactory, VTargetObservable, VDataBindingSelectionDispatcher {

VBindingContext bindingContext;
TableViewer tableViewer;
Class elementType;
Object[] selectionBasePath = null;
TableController tableController;
boolean updatingSelection = false;
List<PropertyListenerSupport> elementPropertyListenerSupportList = new ArrayList<PropertyListenerSupport>();
Map<Object,Object> elementListenerTargetToElementMap = new HashMap<Object, Object>();
List input;
boolean offsetSelection = false;

PropertyChangeListener elementPropertyChangeListener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		tableViewer.refresh(elementListenerTargetToElementMap.get(evt.getSource()));
		tableViewer.getTable().redraw();
	}
};

public VTableViewerObservableList(VBindingContext bindingContext, TableViewer tableViewer, Class elementType) {
	super(SWTObservables.getRealm(tableViewer.getControl().getDisplay()));
	this.bindingContext = bindingContext;
	this.tableViewer = tableViewer;
	
	tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (selectionBasePath == null) return;

			try {
				updatingSelection = true;

				VBindingSelection selection = getSelection();
				if (selection != null)
					((VDataBindingSelectionProvider)VTableViewerObservableList.this.bindingContext.getSelectionProvider()).fireSelectionChanged(getSelection());
			} finally {
				updatingSelection = false;
			}
		}
	});
}

public boolean isResponsibleFor(Control control) {
	return control == tableViewer.getControl();
}


protected int doGetSize() {
	if (tableViewer.getInput() == null) return 0;
	return ((IStructuredContentProvider)tableViewer.getContentProvider()).getElements(tableViewer.getInput()).length;
}

int getIndexOfElement(Object element) {
	Object[] elements = ((IStructuredContentProvider)tableViewer.getContentProvider()).getElements(tableViewer.getInput());
	for (int i=0; i<elements.length; i++)
		if (element.equals(elements[i]))
			return i;
	return -1;
}

public Object get(int index) {
	return ((IStructuredContentProvider)tableViewer.getContentProvider()).getElements(tableViewer.getInput())[index];
}

public void add(int index, Object element) {
	// update slave list
	((List)tableViewer.getInput()).add(index, element);
	addElementChangedListener(element);
	tableViewer.insert(element, index);
}

public boolean remove(Object o) {
	// update slave list
	((List)tableViewer.getInput()).remove(o);
	removeElementChangedListener(o);
	tableViewer.remove(o);
	return true;
}

public synchronized void dispose() {
	for (Object o : this.input) {
		removeElementChangedListener(o);
	}
}

private void addElementChangedListener(Object o) {
	if (!tableController.hookElementChangeListeners()) return;
	for (PropertyListenerSupport lsup : elementPropertyListenerSupportList) {
		PropertyPathSupport sup = (PropertyPathSupport) lsup.userObject;
		sup.object = o;
		Object target = sup.readTargetObject();
		lsup.hookListener(target);
		elementListenerTargetToElementMap.put(target, o);
	}
}

private void removeElementChangedListener(Object o) {
	if (!tableController.hookElementChangeListeners()) return;
	for (PropertyListenerSupport lsup : elementPropertyListenerSupportList) {
		PropertyPathSupport sup = (PropertyPathSupport) lsup.userObject;
		sup.object = o;
		Object target = sup.readTargetObject();
		lsup.unhookListener(target);
		elementListenerTargetToElementMap.remove(target);
	}
}

public Object remove(int index) {
	// update slave list
	Object removed = ((List)tableViewer.getInput()).remove(index);
	removeElementChangedListener(removed);
	tableViewer.remove(removed);
	return removed;
}

public Object getElementType() {
	return elementType;
}

public TableViewer getTableViewer() {
	return tableViewer;
}

public Widget getWidget() {
	return tableViewer.getTable();
}

public VDataBindingSelectionDispatcher getSelectionDispatcher() {
	return this;
}

public void dispatchSelection(VDataBindingSelectionDispatchContext dispatchCtx) {
	if (updatingSelection) return;
	
	dispatchCtx.markOffset();
	try {
		dispatchCtx.nextPathToken();
		if (dispatchCtx.matchesPath(selectionBasePath)) {
			dispatchCtx.dispatched = true;
			if (dispatchCtx.hasNextPathToken()) {
				int index = (Integer)dispatchCtx.nextPathToken();
				Object selectedElement = new StructuredSelection(tableViewer.getElementAt(index));
				tableViewer.setSelection(new StructuredSelection(selectedElement));
				tableViewer.getTable().setTopIndex(index);
			
				if (tableController != null) {
					tableController.dispatchSelection(index, dispatchCtx);
				}
			}
		}
	} finally {
		if (!(offsetSelection && dispatchCtx.dispatched))
			dispatchCtx.resetOffset();
	}
	if (!dispatchCtx.dispatched)
		dispatchCtx.invokeNextDispatcher(false);
}

public VBindingSelection getSelection() {
	if (tableViewer == null && tableViewer.getTable() == null || tableViewer.getTable().isDisposed()) return null;
	IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection(); 
	if (selection.isEmpty()) return null;
	
	return new VBindingSelection(this, 
			(IStructuredSelection)tableViewer.getSelection(), 
			new SelectionPath(selectionBasePath, new Object[]{indexOf(selection.getFirstElement())}));
}

public void setSelectionBasePath(Object[] qualifiers) {
	selectionBasePath = qualifiers;
}

public TableController getTableController() {
	return tableController;
}

public void setTableController(TableController tableController) {
	this.tableController = tableController;
}

public EnabledObservableValue createEnabledObservableValue() {
	return new VTableViewerEnabledObservableValue(this);
}

public void initializePropertyChangeListeners() {
	if (!tableController.hookElementChangeListeners()) return;
	Collection<String> propertyPathes = tableController.getPropertyPathes();
	for (String path : propertyPathes) {
		PropertyPathSupport ps = new PropertyPathSupport();
		ps.propertyPath = path;
		ps.initialize();
		PropertyListenerSupport listenerSup = new PropertyListenerSupport(elementPropertyChangeListener, ps.getLastPathComponent());
		listenerSup.userObject = ps;
		elementPropertyListenerSupportList.add(listenerSup);
	}
}

public void setSelectionTargetControl(Control control) {
	throw new IllegalArgumentException();
}

public void setInput(List list) {
	tableViewer.setInput(list);
	this.input = list;
}

public void extensionsInstalled() {
	tableController.extensionsInstalled();
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	throw new IllegalStateException("TableViewers do not support context selection support");
}

public boolean blockDefaultTraversing() {
	return false;
}

public void setOffsetSelection(boolean offsetSelection) {
	this.offsetSelection = offsetSelection;
}

}
