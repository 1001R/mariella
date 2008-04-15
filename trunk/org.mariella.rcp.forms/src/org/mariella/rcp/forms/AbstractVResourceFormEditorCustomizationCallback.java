package org.mariella.rcp.forms;

import org.mariella.rcp.resources.VResourceEditorPart;
import org.mariella.rcp.resources.VResourceSaveException;

public abstract class AbstractVResourceFormEditorCustomizationCallback implements VResourceFormEditorCustomizationCallback {

AbstractVResourceFormEditor formEditor;

public void aboutToCloseEditor() {}

public void aboutToSave() {}

public void activated() {}

public void deactivated() {}

public void implementDispose() {}

public void implementInit() {}

public final void implementRefresh(boolean layout) {
	for (VResourceFormPage page : formEditor.getPages()) {
		page.refresh(layout);
	}
}

public void implementSetFocus() {}

public void setResourceEditorPart(VResourceEditorPart editorPart) {
	this.formEditor = (AbstractVResourceFormEditor)editorPart;
}

public Object createCustomEditingContext() {
	return null;
}

@Override
public boolean handleVResourceSaveException(VResourceSaveException ex) {
	return false;
}

}
