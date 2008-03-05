package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;

public class VStatusLineManagerErrorMsgAdapter extends AbstractObservableValue {

IStatusLineManager statusLineMgr;

public VStatusLineManagerErrorMsgAdapter(IStatusLineManager mgr) {
	this.statusLineMgr = mgr;
}

protected Object doGetValue() {
	throw new UnsupportedOperationException();
}

public Object getValueType() {
	return String.class;
}

protected void doSetValue(Object value) {
	if ("OK".equals(value))
		statusLineMgr.setErrorMessage("");
	else {
		statusLineMgr.setErrorMessage((String)value);
		Display.getCurrent().beep();
	}
}

}
