package org.mariella.rcp.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractVResourceEditorSupport {
private static Log log = LogFactory.getLog(AbstractVResourceEditorSupport.class);

protected VResourceEditorPart editorPart;
protected VResourceEditorCustomizationCallback customizationCallback;
protected boolean refreshing = false;
protected boolean dirty = false;

VResourceChangeListener resourceChangeListener = new VResourceChangeListener() {
	public void resourceChanged(VResourceChangeEvent event) {
		// automatic dirty setting only if modification event comes from the resource
		if (event.getSource() == event.getResource() && event.getResource() == getEditorInput())
			setDirty();
	}
	public void resourceRemoved(VResourceChangeEvent event) {
		if (event.getResource() == getEditorInput())
			throw new IllegalStateException("A resource should not be removed from pool when a corresponding editor is open");
	}
	public void resourceLoaded(VResourceChangeEvent event) {
		if (event.getResource() == getEditorInput())
			throw new IllegalStateException("A resource should not be loaded when a corresponding editor is open");
	}
};

IPerspectiveListener2 perspectiveListener = new IPerspectiveListener2() {
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
		if (changeId.equals(IWorkbenchPage.CHANGE_EDITOR_CLOSE) && partRef.getPart(false) == editorPart)
			customizationCallback.aboutToCloseEditor();
	}
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {}
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}
};

IPartListener partListener = new IPartListener() {
	public void partActivated(IWorkbenchPart part) {
		customizationCallback.activated();
	}
	public void partDeactivated(IWorkbenchPart part) {
		customizationCallback.deactivated();
	}

	public void partOpened(IWorkbenchPart part) {}

	public void partClosed(IWorkbenchPart part) {}

	public void partBroughtToTop(IWorkbenchPart part) {}
};

public AbstractVResourceEditorSupport(VResourceEditorPart editorPart) {
	this.editorPart = editorPart;
	this.customizationCallback = editorPart.createCustomizationCallback();
	this.customizationCallback.setResourceEditorPart(editorPart);
}


protected IEditorInput getEditorInput() {
	return editorPart.getEditorInput();
}

public void setDirty() {
	if (refreshing) return;
	
	dirty = true;
	editorPart.firePropertyChange(IEditorPart.PROP_DIRTY);
	
}

public void implementDoSave(IProgressMonitor monitor) {
	try {
		customizationCallback.aboutToSave();
		customizationCallback.getResourceManager().saveResource(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), customizationCallback.getResource());
		editorPart.setPartName(getEditorInput().getName());
	} catch (VResourceSaveException e) {
		handleVResourceSaveException(e);
		return;
	}
	refresh(false);
	resetDirty();
	editorPart.firePropertyChange(IEditorPart.PROP_DIRTY);
}

protected void handleVResourceSaveException(VResourceSaveException ex) {
	if (customizationCallback.handleVResourceSaveException(ex))
		return;
	
	log.debug("Could not save resource", ex);
	MessageDialog.openError(editorPart.getSite().getShell(), "Resource kann nicht gespeichert werden", ex.getMessage());
}

public void resetDirty() {
	dirty = false;
}

public void refresh(boolean layout) {
	refreshing = true;
	try {
		customizationCallback.implementRefresh(layout);
	} finally {
		refreshing = false;
	}

}

public void implementInit(IEditorSite site, IEditorInput input) throws PartInitException {
	editorPart.setSite(site);
	editorPart.setInput(input);
	editorPart.setPartName(input.getName());

	doVResourceInitStuff();
}

protected void doVResourceInitStuff() {
	customizationCallback.getResourceManager().addReferrer(customizationCallback.getResource().getRef(), this);
	customizationCallback.implementInit();	
	
	editorPart.getSite().getPage().addPartListener(partListener);
	editorPart.getSite().getWorkbenchWindow().addPerspectiveListener(perspectiveListener);
	VResourcesPlugin.getResourcePool().addResourceChangeListener(resourceChangeListener);
}

public void implementDispose() {
	customizationCallback.implementDispose();
	VResourcesPlugin.getResourcePool().removeResourceChangeListener(resourceChangeListener);
	customizationCallback.getResourceManager().removeReferrer(this);
	if (dirty)
		customizationCallback.getResourceManager().reload(customizationCallback.getResource().getRef());

	editorPart.getSite().getPage().removePartListener(partListener);
	editorPart.getSite().getWorkbenchWindow().removePerspectiveListener(perspectiveListener);
}

public void implementSetFocus() {
	VResourcesPlugin.getResourceSelectionManager(editorPart.getEditorSite().getWorkbenchWindow()).retreivedFocus(editorPart);
	customizationCallback.implementSetFocus();
}

public boolean isDirty() {
	return dirty;
}


}
