package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Button;

public class VButtonObservableValue extends VAbstractButtonObservableValue {

public VButtonObservableValue(Button button) {
	super(button);
}

@Override
protected boolean isSelectedValue(Object value) {
	return Boolean.TRUE.equals(value);
}

@Override
public Object doGetValue() {
	return button.getSelection() ? Boolean.TRUE : Boolean.FALSE;
}

@Override
public Object getValueType() {
	return Boolean.class;
}
}

