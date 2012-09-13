package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.util.RowLayout;

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
	Control control = (Control)getWidget();
	if (control.getParent().getLayout() instanceof RowLayout) {
		return ((RowLayout)control.getParent().getLayout()).isVisible(control);
	} else {
		return control.isVisible();
	}
}

@Override
protected void doSetValue(Object value) {
	Control control = (Control)getWidget();
	if (control.getParent().getLayout() instanceof RowLayout) {
		((RowLayout)control.getParent().getLayout()).setVisible(control, (Boolean)value);
	}	else {
		((Control)getWidget()).setVisible((Boolean)value);
	}
	parentToRedraw.layout(true, true);
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
