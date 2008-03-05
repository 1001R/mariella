package org.mariella.rcp.resources;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public abstract class AbstractVResourceMultiPageEditor extends MultiPageEditorPart implements VResourceEditorPart {

VResourceMultiEditorSupport editorSupport = new VResourceMultiEditorSupport(this);

protected final void createPages() {
	editorSupport.implementCreatePages();
}

public final void dispose() {
	super.dispose();
	editorSupport.implementDispose();
}

public final void doSave(IProgressMonitor monitor) {
	editorSupport.implementDoSave(monitor);
}

public final void doSaveAs() {
	throw new UnsupportedOperationException();
}

public final void firePropertyChange(int propertyId) {
	super.firePropertyChange(propertyId);
}

public abstract boolean hasUserEditorAccess();

public final void init(IEditorSite site, IEditorInput input) throws PartInitException {
	editorSupport.implementInit(site, input);
}

public final boolean isDirty() {
	return editorSupport.isDirty();
}

public final boolean isSaveAsAllowed() {
	return false;
}

public final void setFocus() {
	editorSupport.implementSetFocus();
}

public final void setInput(IEditorInput input) {
	super.setInput(input);
}

public final void setPartName(String partName) {
	super.setPartName(partName);
}

public final void setSite(IEditorSite site) {
	super.setSite(site);
}
}
