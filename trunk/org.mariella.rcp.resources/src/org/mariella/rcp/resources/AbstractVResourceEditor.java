package org.mariella.rcp.resources;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class AbstractVResourceEditor extends EditorPart implements VResourceEditorPart {

VResourceEditorSupport editorSupport = new VResourceEditorSupport(this);

@Override
public final void createPartControl(Composite parent) {
	editorSupport.implementCreatePartControl(parent);
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
public final void init(IEditorSite site, IEditorInput input) throws PartInitException {
	editorSupport.implementInit(site, input);
}

@Override
public boolean isDirty() {
	return editorSupport.isDirty();
}

@Override
public final void setFocus() {
	editorSupport.implementSetFocus();
}

public void aboutToCloseEditor() {}

public void activated() {}

public void deactivated() {}

@Override
public void doSaveAs() {
	throw new UnsupportedOperationException();
}

public abstract boolean hasUserEditorAccess();

@Override
public boolean isSaveAsAllowed() {
	return false;
}

public void implementDispose() {}

public void implementInit() {}

@Override
public void setInput(IEditorInput input) {
	super.setInput(input);
}

@Override
public void setPartName(String partName) {
	super.setPartName(partName);
}

@Override
public void firePropertyChange(int propertyId) {
	super.firePropertyChange(propertyId);
}

public void refresh(boolean layout) {
	editorSupport.refresh(layout);
}

public void setDirty() {
	editorSupport.setDirty();
}

public void setSite(IEditorSite site) {
	super.setSite(site);
}

}
