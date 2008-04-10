package org.mariella.rcp.resources;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public abstract class AbstractVResourceMultiPageEditor extends MultiPageEditorPart implements VResourceEditorPart {

VResourceMultiEditorSupport editorSupport = new VResourceMultiEditorSupport(this);

@Override
protected final void createPages() {
	editorSupport.implementCreatePages();
}

@Override
public final void dispose() {
	super.dispose();
	editorSupport.implementDispose();
}

@Override
public final void doSave(IProgressMonitor monitor) {
	editorSupport.implementDoSave(monitor);
}

@Override
public final void doSaveAs() {
	throw new UnsupportedOperationException();
}

@Override
public final void firePropertyChange(int propertyId) {
	super.firePropertyChange(propertyId);
}

public abstract boolean hasUserEditorAccess();

@Override
public final void init(IEditorSite site, IEditorInput input) throws PartInitException {
	editorSupport.implementInit(site, input);
}

@Override
public final boolean isDirty() {
	return editorSupport.isDirty();
}

@Override
public final boolean isSaveAsAllowed() {
	return false;
}

@Override
public final void setFocus() {
	editorSupport.implementSetFocus();
}

@Override
public final void setInput(IEditorInput input) {
	super.setInput(input);
}

@Override
public final void setPartName(String partName) {
	super.setPartName(partName);
}

public final void setSite(IEditorSite site) {
	super.setSite(site);
}
}
