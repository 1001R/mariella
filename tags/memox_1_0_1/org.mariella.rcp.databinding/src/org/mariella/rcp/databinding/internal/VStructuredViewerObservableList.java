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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingContextObserver;
import org.mariella.rcp.databinding.VBindingSelection;

public abstract class VStructuredViewerObservableList extends AbstractObservableList implements ISWTObservable, SelectionAwareObservable, VTargetObservable, VDataBindingSelectionDispatcher, VBindingContextObserver {

VBindingContext bindingContext;
StructuredViewer viewer;
Class elementType;
Object[] selectionBasePath = null;
StructuredViewerController controller;
boolean updatingSelection = false;
List<PropertyListenerSupport> elementPropertyListenerSupportList = new ArrayList<PropertyListenerSupport>();
Map<Object,Object> elementListenerTargetToElementMap = new HashMap<Object, Object>();
List input;
boolean offsetSelection = false;
IStructuredSelection selectionOnUpdateTargets = null;

PropertyChangeListener elementPropertyChangeListener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		viewer.refresh(elementListenerTargetToElementMap.get(evt.getSource()));
		viewer.getControl().redraw();
	}
};

public VStructuredViewerObservableList(VBindingContext bindingContext, StructuredViewer structuredViewer, Class elementType) {
	super(SWTObservables.getRealm(structuredViewer.getControl().getDisplay()));
	this.bindingContext = bindingContext;
	this.viewer = structuredViewer;
	bindingContext.addObserver(this);
	
	structuredViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (selectionBasePath == null) return;

			try {
				updatingSelection = true;

				VBindingSelection selection = getSelection();
				if (selection != null)
					((VBindingSelectionProvider)VStructuredViewerObservableList.this.bindingContext.getSelectionProvider()).fireSelectionChanged(selection);
			} finally {
				updatingSelection = false;
			}
		}
	});
}

public boolean isResponsibleFor(Control control) {
	return control == viewer.getControl();
}

@Override
protected int doGetSize() {
	if (viewer.getInput() == null) return 0;
	return ((IStructuredContentProvider)viewer.getContentProvider()).getElements(viewer.getInput()).length;
}

int getIndexOfElement(Object element) {
	Object[] elements = ((IStructuredContentProvider)viewer.getContentProvider()).getElements(viewer.getInput());
	for (int i=0; i<elements.length; i++)
		if (element.equals(elements[i]))
			return i;
	return -1;
}

@Override
public Object get(int index) {
	return ((IStructuredContentProvider)viewer.getContentProvider()).getElements(viewer.getInput())[index];
}

@Override
public void add(int index, Object element) {
	// update slave list
	((List)viewer.getInput()).add(index, element);
	addElementChangedListener(element);
	implementInsert(element, index);
}

protected abstract void implementInsert(Object element, int index);

@Override
public boolean remove(Object o) {
	// update slave list
	((List)viewer.getInput()).remove(o);
	removeElementChangedListener(o);
	implementRemove(o);
	return true;
}

protected abstract void implementRemove(Object element);

@Override
public synchronized void dispose() {
	for (Object o : this.input) {
		removeElementChangedListener(o);
	}
}

private void addElementChangedListener(Object o) {
	if (!controller.hookElementChangeListeners()) return;
	for (PropertyListenerSupport lsup : elementPropertyListenerSupportList) {
		PropertyPathSupport sup = (PropertyPathSupport) lsup.userObject;
		sup.object = o;
		Object target = sup.readTargetObject();
		lsup.hookListener(target);
		elementListenerTargetToElementMap.put(target, o);
	}
}

private void removeElementChangedListener(Object o) {
	if (!controller.hookElementChangeListeners()) return;
	for (PropertyListenerSupport lsup : elementPropertyListenerSupportList) {
		PropertyPathSupport sup = (PropertyPathSupport) lsup.userObject;
		sup.object = o;
		Object target = sup.readTargetObject();
		lsup.unhookListener(target);
		elementListenerTargetToElementMap.remove(target);
	}
}

@Override
public Object remove(int index) {
	// update slave list
	Object removed = ((List)viewer.getInput()).remove(index);
	removeElementChangedListener(removed);
	implementRemove(removed);
	return removed;
}

public Object getElementType() {
	return elementType;
}

public StructuredViewer getViewer() {
	return viewer;
}

public Widget getWidget() {
	return viewer.getControl();
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
				viewer.setSelection(new StructuredSelection(implementGetElementAt(index)));
				implementSetTopIndex(index);
				
				
				completeDispatchSelection(index, dispatchCtx);
			}
		}
	} finally {
		if (!(offsetSelection && dispatchCtx.dispatched))
			dispatchCtx.resetOffset();
	}
	if (!dispatchCtx.dispatched)
		dispatchCtx.invokeNextDispatcher(false);
}

void completeDispatchSelection(int index, VDataBindingSelectionDispatchContext dispatchCtx) {}

protected abstract Object implementGetElementAt(int index);

protected abstract void implementSetTopIndex(int index);

public VBindingSelection getSelection() {
	if (viewer == null && viewer.getControl() == null || viewer.getControl().isDisposed()) return null;
	IStructuredSelection selection = (IStructuredSelection)viewer.getSelection(); 
	if (selection.isEmpty()) return null;
	
	return new VBindingSelection(this, 
			selection, 
			new SelectionPath(selectionBasePath, new Object[]{indexOf(selection.getFirstElement())}));
}

public void setSelectionBasePath(Object[] qualifiers) {
	selectionBasePath = qualifiers;
}

public StructuredViewerController getController() {
	return controller;
}

public void setController(StructuredViewerController controller) {
	this.controller = controller;
}

public void initializePropertyChangeListeners() {
	if (!controller.hookElementChangeListeners()) return;
	Collection<String> propertyPathes = controller.getPropertyPathes();
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
	viewer.setInput(list);
	this.input = list;
}

public void extensionsInstalled() {
	controller.extensionsInstalled();
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	throw new IllegalStateException("StructuredViewers do not support context selection support");
}

public boolean blockDefaultTraversing() {
	return false;
}

public void setOffsetSelection(boolean offsetSelection) {
	this.offsetSelection = offsetSelection;
}


public void aboutToUpdateModelToTarget() {
	selectionOnUpdateTargets = (IStructuredSelection)viewer.getSelection();
}

public void finishedUpdateModelToTarget() {
	if (selectionOnUpdateTargets != null) {
		viewer.setSelection(selectionOnUpdateTargets);
	}
}

@Override
public void aboutToDispose() {}

}
