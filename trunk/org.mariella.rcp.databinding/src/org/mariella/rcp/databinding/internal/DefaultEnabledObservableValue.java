package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public class DefaultEnabledObservableValue extends AbstractSWTObservableValue implements EnabledObservableValue, VTargetObservable {

public DefaultEnabledObservableValue(Widget widget) {
	super(widget);
}

public Object getValueType() {
	return Boolean.class;
}

protected Object doGetValue() {
	throw new UnsupportedOperationException();
}

protected void doSetValue(Object value) {
	((Control)getWidget()).setEnabled((Boolean)value);
}

@Override
public void extensionsInstalled() {
	// TODO Auto-generated method stub
	
}

}
