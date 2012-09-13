package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.widgets.Control;

public class ControlEnabledObservableValue extends AbstractSWTObservableValue implements VTargetObservable {

public ControlEnabledObservableValue(Control control) {
	super(control);
}

@Override
public Object getValueType() {
	return Boolean.class;
}

@Override
protected Object doGetValue() {
	return ((Control)getWidget()).isEnabled();
}

@Override
protected void doSetValue(Object value) {
	((Control)getWidget()).setEnabled((Boolean)value);
}

public boolean blockDefaultTraversing() {
	return false;
}

public void extensionsInstalled() {}

public boolean isResponsibleFor(Control control) {
	return control == getWidget();
}

}
