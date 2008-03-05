package org.mariella.rcp.databinding;

import org.eclipse.ui.forms.editor.FormEditor;

public class FormPageSelectionExtension implements DataBindingContextExtension {

FormEditor formEditor;

public FormPageSelectionExtension(FormEditor formEditor) {
	this.formEditor = formEditor;
}

public void install(VDataBindingContext dataBindingContext) {
	dataBindingContext.selectionProvider.installFormPageSelection(this);
	dataBindingContext.setActionBars(formEditor.getEditorSite().getActionBars());
}

public FormEditor getFormEditor() {
	return formEditor;
}

public void dispose() {}

}
