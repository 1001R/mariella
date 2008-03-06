package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.widgets.Control;

public class VTextViewerEnabledObservableValue extends AbstractSWTObservableValue implements EnabledObservableValue, VTargetObservable {
private VTextViewerObservableValue textViewerObservable;

public VTextViewerEnabledObservableValue(VTextViewerObservableValue textViewerObservable) {
	super(textViewerObservable.textViewer.getTextWidget());
	
	this.textViewerObservable = textViewerObservable;
}

public boolean isResponsibleFor(Control control) {
	return textViewerObservable.isResponsibleFor(control);
}


protected Object doGetValue() {
	throw new UnsupportedOperationException();
}

public Object getValueType() {
	return null;
}

protected void doSetValue(Object value) {
	textViewerObservable.setEnabled((Boolean)value);
}

public void extensionsInstalled() {
}

public boolean blockDefaultTraversing() {
	return false;
}

}
