package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public class DefaultEnabledObservableValue extends AbstractSWTObservableValue implements EnabledObservableValue, VTargetObservable {

public DefaultEnabledObservableValue(Widget widget) {
	super(widget);
}

public boolean isResponsibleFor(Control control) {
	return control == getWidget();
}

@Override
public Object getValueType() {
	return Boolean.class;
}

@Override
protected Object doGetValue() {
	throw new UnsupportedOperationException();
}

@Override
protected void doSetValue(Object value) {
	((Control)getWidget()).setEnabled((Boolean)value);
}

public void extensionsInstalled() {
}

public boolean blockDefaultTraversing() {
	return false;
}

}
