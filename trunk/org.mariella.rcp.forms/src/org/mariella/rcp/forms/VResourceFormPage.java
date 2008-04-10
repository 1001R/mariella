package org.mariella.rcp.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public abstract class VResourceFormPage extends FormPage {

private Object customEditingContext;
private ISelectionProvider selectionProvider = null;
List<VResourceSectionPart> resourceSectionParts = new ArrayList<VResourceSectionPart>();

public VResourceFormPage(String id, String title) {
	super(id, title);
}

public VResourceFormPage(FormEditor editor, String id, String title) {
	super(editor, id, title);
}

@Override
protected final void createFormContent(IManagedForm managedForm) {
	implementCreateFormContent(managedForm);
	refresh(false);
}

protected abstract void implementCreateFormContent(IManagedForm managedForm);

public void refresh(boolean layout) {
	if (getManagedForm() == null) return;
	
	if (layout)
		getManagedForm().reflow(true);
	getManagedForm().refresh();
}

public Object getCustomEditingContext() {
	return customEditingContext;
}

public void setCustomEditingContext(Object customEditingContext) {
	this.customEditingContext = customEditingContext;
}

public ISelectionProvider getSelectionProvider() {
	return selectionProvider;
}

public void setSelectionProvider(ISelectionProvider selectionProvider) {
	this.selectionProvider = selectionProvider;
}

@Override
public void setActive(boolean active) {
	boolean oldActive = isActive();
	super.setActive(active);
	if (active != oldActive) {
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				setFocus();
			}
		});
	}
}

@Override
public void setFocus() {
	if (resourceSectionParts.size() > 0)
		resourceSectionParts.get(0).setFocus();
}
}
