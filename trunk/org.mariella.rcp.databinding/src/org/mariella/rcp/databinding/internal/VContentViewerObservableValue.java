package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.swt.widgets.Control;

public class VContentViewerObservableValue extends AbstractObservableValue implements VTargetObservable {

ContentViewer contentViewer;
Class valueType;

public VContentViewerObservableValue(ContentViewer contentViewer, Class valueType) {
	this.contentViewer = contentViewer;
	this.valueType = valueType;
}

public boolean isResponsibleFor(Control control) {
	return control == contentViewer.getControl();
}

protected Object doGetValue() {
	return contentViewer.getInput();
}

protected void doSetValue(Object value) {
	contentViewer.setInput(value);
}

public Object getValueType() {
	return valueType;
}

public void extensionsInstalled() {
}

public boolean blockDefaultTraversing() {
	return false;
}

}
