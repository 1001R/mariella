package org.mariella.rcp.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mariella.rcp.resources.VResourceEditorPart;


public abstract class AbstractVResourceFormEditor extends FormEditor implements VResourceEditorPart {

VResourceFormEditorSupport editorSupport = new VResourceFormEditorSupport(this);
VFormToolkit vFormToolkit = null;

protected void addPages() {
	editorSupport.implementAddPages();
}

public void doSave(IProgressMonitor monitor) {
	editorSupport.implementDoSave(monitor);
}

public void init(IEditorSite site, IEditorInput input) throws PartInitException {
	editorSupport.implementInit(site, input);
}

public void dispose() {
	editorSupport.implementDispose();
	super.dispose();
}

public void doSaveAs() {
	throw new UnsupportedOperationException();
}

public boolean isSaveAsAllowed() {
	return false;
}


public final void setDirtyStateChanged() {
	for (VResourceFormPage page : (Vector<VResourceFormPage>)pages) {
		if (page != null && page.getManagedForm() != null)
			page.getManagedForm().dirtyStateChanged();
	}
}

public final void setInput(IEditorInput input) {
	super.setInput(input);
}

public final void setSite(IEditorSite site) {
	super.setSite(site);
}

public final void setPartName(String partName) {
	super.setPartName(partName);
}

public final void firePropertyChange(int propertyId) {
	super.firePropertyChange(propertyId);
}

public boolean isDirty() {
	return editorSupport.isDirty();
}

public List<VResourceFormPage> getPages() {
	ArrayList<VResourceFormPage> result = new ArrayList<VResourceFormPage>();
	for (FormPage page : (Vector<FormPage>)pages) {
		if (page != null)
			result.add((VResourceFormPage)page);
	}
	return result;
}

protected void configurePage(int index, IFormPage page) throws PartInitException {
	super.configurePage(index, page);
	editorSupport.implementConfigurePage(index, page);
}

public final void setFocus() {
	editorSupport.implementSetFocus();
}

void hideTabsIfNeeded() {
	if (getPageCount() <= 1) {
		setPageText(0, "");
		if (getContainer() instanceof CTabFolder) {
			((CTabFolder)getContainer()).setTabHeight(0);
		}
	}
}

protected FormToolkit createToolkit(Display display) {
	return new FormToolkit(new FormColors(display));
}

public VFormToolkit getVFormToolkit() {
	if (vFormToolkit == null) {
		vFormToolkit = new VFormToolkit(getToolkit());
	}
	return vFormToolkit;
}
}
