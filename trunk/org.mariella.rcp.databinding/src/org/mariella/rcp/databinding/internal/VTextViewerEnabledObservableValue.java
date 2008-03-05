package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;

public class VTextViewerEnabledObservableValue extends AbstractSWTObservableValue implements EnabledObservableValue, VTargetObservable {
private VTextViewerObservableValue textViewerObservable;

public VTextViewerEnabledObservableValue(VTextViewerObservableValue textViewerObservable) {
	super(textViewerObservable.textViewer.getTextWidget());
	
	this.textViewerObservable = textViewerObservable;
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

@Override
public void extensionsInstalled() {
	// TODO Auto-generated method stub
	
}

}
