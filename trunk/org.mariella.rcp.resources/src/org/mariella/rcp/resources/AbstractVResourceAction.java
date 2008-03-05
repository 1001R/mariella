package org.mariella.rcp.resources;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public abstract class AbstractVResourceAction extends Action implements IWorkbenchAction, VResourceSelectionListener, VResourceChangeListener {

private IWorkbenchWindow workbenchWindow;
private IWorkbenchPart selectedPart;
private List<VManagedSelectionItem> selectedItems;

public AbstractVResourceAction(IWorkbenchWindow window) {
	super();
	this.workbenchWindow = window;
	hookListeners();
}

public AbstractVResourceAction(IWorkbenchWindow window, String text, ImageDescriptor image) {
	super(text, image);
	this.workbenchWindow = window;
	hookListeners();
}

public AbstractVResourceAction(IWorkbenchWindow window, String text, int style) {
	super(text, style);
	this.workbenchWindow = window;
	hookListeners();
}

public AbstractVResourceAction(IWorkbenchWindow window, String text) {
	super(text);
	this.workbenchWindow = window;
	hookListeners();
}

protected void hookListeners() {
	VResourcesPlugin.getResourceSelectionManager(workbenchWindow).addSelectionListener(this);
	VResourcesPlugin.getResourcePool().addResourceChangeListener(this);
}

public void dispose() {
}

public void selectionChanged(VResourceSelectionEvent event) {
	this.selectedPart = event.getSelection().getPart();
	this.selectedItems = event.getSelection().getSelectedItems();
	setEnabled(calculateEnabled());
}

public void resourceChanged(VResourceChangeEvent event) {
	if (mustRevalidate(event))
		setEnabled(calculateEnabled());
}

protected boolean mustRevalidate(VResourceChangeEvent event) {
	return this.selectedItems instanceof VResourceRefHolder &&
		((VResourceRefHolder)this.selectedItems).getRef().equals(event.getResource().getRef());
}

public void resourceRemovedFromPool(VResourceChangeEvent event) {
	if (this.selectedItems instanceof VResourceRefHolder &&
			((VResourceRefHolder)this.selectedItems).getRef().equals(event.getResource().getRef()))
		setEnabled(calculateEnabled());
}

public void resourceLoaded(VResourceChangeEvent event) {}

protected abstract boolean calculateEnabled();

public VManagedSelectionItem getSelectedItem() {
	if (selectedItems == null || selectedItems.isEmpty())
		return null;
	return selectedItems.get(0);
}

public List<VManagedSelectionItem> getSelectedItems() {
	if (selectedItems == null) return Collections.EMPTY_LIST;
	return selectedItems;
}

public VResourceRefHolder getSelectedRefHolder() {
	return (VResourceRefHolder)getSelectedItem();
}

public IWorkbenchWindow getWindow() {
	return workbenchWindow;
}

public IWorkbenchPart getSelectedPart() {
	return selectedPart;
}
}
