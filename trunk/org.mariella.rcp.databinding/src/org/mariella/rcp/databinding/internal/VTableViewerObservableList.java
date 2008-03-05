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
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VDataBindingContext;

public class VTableViewerObservableList extends AbstractObservableList implements ISWTObservable, SelectionAwareObservable, EnabledObservableValueFactory, VTargetObservable {

VDataBindingContext dataBindingContext;
TableViewer tableViewer;
Class elementType;
Object[] selectionBasePath = null;
TableController tableController;
boolean updatingSelection = false;
List<PropertyListenerSupport> elementPropertyListenerSupportList = new ArrayList<PropertyListenerSupport>();
Map<Object,Object> elementListenerTargetToElementMap = new HashMap<Object, Object>();
List input;

PropertyChangeListener elementPropertyChangeListener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		tableViewer.refresh(elementListenerTargetToElementMap.get(evt.getSource()));
		tableViewer.getTable().redraw();
	}
};

public VTableViewerObservableList(VDataBindingContext dataBindingContext, TableViewer tableViewer, Class elementType) {
	super(SWTObservables.getRealm(tableViewer.getControl().getDisplay()));
	this.dataBindingContext = dataBindingContext;
	this.tableViewer = tableViewer;
	
	tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (selectionBasePath == null) return;

			try {
				updatingSelection = true;

				VDataBindingSelection selection = getSelection();
				if (selection != null)
					((VDataBindingSelectionProvider)VTableViewerObservableList.this.dataBindingContext.getSelectionProvider()).fireSelectionChanged(getSelection());
			} finally {
				updatingSelection = false;
			}
		}
	});

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
	return remove(tableViewer.getElementAt(index));
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

public boolean dispatchSelectionPath(SelectionPath path, int offset) {
	if (updatingSelection) return false;
	if (!hasBasePath(path, offset)) return false;
	
	offset += selectionBasePath.length;
	if (offset >= path.getQualifiers().length) return false;
	
	int index = (Integer)path.getQualifiers()[offset];
	tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(index)));
	tableViewer.getTable().setTopIndex(index);
	
	offset++;
	if (tableController != null && offset < path.getQualifiers().length) {
		tableController.dispatchSelectionPath(path, offset, index);
	}
	
	return true;
}

private boolean hasBasePath(SelectionPath path, int offset) {
	for (int i=offset, c = 0; i<offset+selectionBasePath.length; i++, c++)
		if (!path.getQualifiers()[i].equals(selectionBasePath[c]))	return false;
	return true;
}

public VDataBindingSelection getSelection() {
	if (tableViewer == null && tableViewer.getTable() == null || tableViewer.getTable().isDisposed()) return null;
	int index = tableViewer.getTable().getSelectionIndex();
	if (index == -1) return null;
	
	return new VDataBindingSelection(this, new SelectionPath(selectionBasePath, new Object[]{index}));
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

}
