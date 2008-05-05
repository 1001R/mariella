package org.mariella.rcp.resources;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractEditorAwareVResourceManager extends AbstractVResourceManager {

private String editorId;
private String elementFactoryId;

public AbstractEditorAwareVResourceManager(String editorId, String elementFactoryId) {
	this.editorId = editorId;
	this.elementFactoryId = elementFactoryId;
}

public AbstractEditorAwareVResourceManager() {
}

public void openEditor(final IWorkbenchWindow window, final Object persistentId) {
	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
		public void run() {
			openEditor(window, getResourceForPersistentId(persistentId));
		}
	});
}

public void openEditor(final IWorkbenchWindow window, final VResourceRef ref) {
	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
		public void run() {
			openEditor(window, getResource(ref));
		}
	});
}

public void openEditor(final IWorkbenchWindow window, final VResource resource) {
	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
		public void run() {
			IEditorInput editorInput = getEditorInput(resource);
			try {
				window.getActivePage().openEditor(editorInput, editorId);
			} catch (PartInitException e) {
				throw new RuntimeException(e);
			}
		}
	});
}

public boolean removeResource(VResourceRef ref)	throws VResourceSaveException {
	VResource resource = getResource(ref);
	return removeResource(resource);
}

@Override
public boolean removeResource(VResource resource)	throws VResourceSaveException {
	String name = "'" + resource.getName() + "'";
	if (!MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), name + "wirklich l�schen?", "Wollen Sie " + name + " wirklich l�schen?"))
		return false;
	IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
	for (IWorkbenchWindow window : windows) {
		IEditorInput editorInput = getEditorInput(resource);
		IEditorPart editor = window.getActivePage().findEditor(editorInput);
		if (editor != null) {
			if (!window.getActivePage().closeEditor(editor, false)) {
				return false;
			}
		}
	}
	
	return super.removeResource(resource);
}

public abstract IEditorInput getEditorInput(VResource resource);

public String getEditorId() {
	return editorId;
}

public void setEditorId(String editorId) {
	this.editorId = editorId;
}

public String getElementFactoryId() {
	return elementFactoryId;
}

public void setElementFactoryId(String elementFactoryId) {
	this.elementFactoryId = elementFactoryId;
}


	
}
