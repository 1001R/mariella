package org.mariella.rcp.forms;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.mariella.rcp.resources.AbstractVResourceEditorSupport;
import org.mariella.rcp.resources.VResourceEditorPart;


public class VResourceFormEditorSupport extends AbstractVResourceEditorSupport {

private Object customEditingContext;

public VResourceFormEditorSupport(VResourceEditorPart editorPart) {
	super(editorPart);
	customEditingContext = customizationCallback.createCustomEditingContext();
}

public void implementAddPages() {
	((VResourceFormEditorCustomizationCallback)customizationCallback).implementAddPages();
	hideTabsIfNeeded();
}

void hideTabsIfNeeded() {
	AbstractVResourceFormEditor editor = (AbstractVResourceFormEditor)editorPart;
	editor.hideTabsIfNeeded();
}


public void implementInit(IEditorSite site, IEditorInput input) throws PartInitException {
	super.implementInit(site, input);
}

public void setDirty() {
	if (refreshing) return;
	
	dirty = true;

	((FormEditor)editorPart).editorDirtyStateChanged();
}

public void implementConfigurePage(int index, IFormPage page) {
	((VResourceFormPage)page).setCustomEditingContext(customEditingContext);
}

public void implementSetFocus() {
	IFormPage activePage = ((AbstractVResourceFormEditor)editorPart).getActivePageInstance();
	if (activePage != null)
		activePage.setFocus();
}

}
