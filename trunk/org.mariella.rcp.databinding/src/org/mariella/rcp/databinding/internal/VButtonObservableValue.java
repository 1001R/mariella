package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Button;

public class VButtonObservableValue extends VAbstractButtonObservableValue {

public VButtonObservableValue(Button button) {
	super(button);
}

protected boolean isSelectedValue(Object value) {
	return Boolean.TRUE.equals(value);
}

public Object doGetValue() {
	return button.getSelection() ? Boolean.TRUE : Boolean.FALSE;
}

public Object getValueType() {
	return Boolean.class;
}
}

