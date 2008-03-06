package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.FormEditor;

public class FormEditorSelectionObservableValue extends AbstractObservableValue implements VTargetObservable{

FormEditor formEditor;

public FormEditorSelectionObservableValue(FormEditor editor) {
	this.formEditor  = editor;
}

public boolean isResponsibleFor(Control control) {
	return false;
}

protected Object doGetValue() {
	return null;
}

public Object getValueType() {
	return null;
}

public void extensionsInstalled() {
}

public boolean blockDefaultTraversing() {
	return false;
}

}
