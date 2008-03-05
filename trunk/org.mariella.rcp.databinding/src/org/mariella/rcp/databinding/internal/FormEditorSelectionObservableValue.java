package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.ui.forms.editor.FormEditor;

public class FormEditorSelectionObservableValue extends AbstractObservableValue implements VTargetObservable{

FormEditor formEditor;

public FormEditorSelectionObservableValue(FormEditor editor) {
	this.formEditor  = editor;
}

protected Object doGetValue() {
	return null;
}

public Object getValueType() {
	return null;
}

@Override
public void extensionsInstalled() {
	// TODO Auto-generated method stub
	
}

}
