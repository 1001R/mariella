package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ControlVisibleObservableValue extends AbstractSWTObservableValue implements VTargetObservable {

Composite parentToRedraw;

public ControlVisibleObservableValue(Control control, Composite parentToRedraw) {
	super(control);
	this.parentToRedraw = parentToRedraw;
}

@Override
public Object getValueType() {
	return Boolean.class;
}

@Override
protected Object doGetValue() {
	return ((Control)getWidget()).isVisible();
}

@Override
protected void doSetValue(Object value) {
	((Control)getWidget()).setVisible((Boolean)value);
	parentToRedraw.redraw();
}

public boolean blockDefaultTraversing() {
	return false;
}

public void extensionsInstalled() {}

public boolean isResponsibleFor(Control control) {
	return control == getWidget();
}

}
