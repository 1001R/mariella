package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Button;

public class VRadioButtonObservableValue extends VAbstractButtonObservableValue {

class ImproperValue {}

Object matchingValue;

public VRadioButtonObservableValue(Button button, Object matchingValue) {
	super(button);
	this.matchingValue = matchingValue;
}

@Override
protected boolean isSelectedValue(Object value) {
	return matchingValue.equals(value);
}

@Override
protected Object doGetValue() {
	if (!button.getSelection()) 
		return new ImproperValue();
	return matchingValue;
}

@Override
public Object getValueType() {
	return matchingValue.getClass();
}

}
