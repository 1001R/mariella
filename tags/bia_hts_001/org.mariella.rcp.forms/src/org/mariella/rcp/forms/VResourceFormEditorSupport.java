package org.mariella.rcp.forms;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
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


@Override
public void implementInit(IEditorSite site, IEditorInput input) throws PartInitException {
	super.implementInit(site, input);
}

@Override
public void setDirty() {
	if (refreshing) return;
	
	dirty = true;

	((FormEditor)editorPart).editorDirtyStateChanged();
}

public void implementConfigurePage(int index, IFormPage page) {
	((VResourceFormPage)page).setCustomEditingContext(customEditingContext);
}

@Override
public void implementSetFocus() {
	Display.getCurrent().asyncExec(new Runnable() {
		public void run() {
			IFormPage activePage = ((AbstractVResourceFormEditor)editorPart).getActivePageInstance();
			if (activePage != null && !isChild(activePage.getPartControl(), Display.getCurrent().getFocusControl()))
				activePage.setFocus();
		}
	});
}

boolean isChild(Control childOf, Control focusControl) {
	if (focusControl == null) return false;
	Control parent = focusControl;
	while (parent != null) {
		if (parent == childOf) return true;
		parent = parent.getParent();
	}
	return false;
}

void setRefreshing(boolean refreshing) {
	this.refreshing = refreshing;
}

public Object getCustomEditingContext() {
	return customEditingContext;
}

}