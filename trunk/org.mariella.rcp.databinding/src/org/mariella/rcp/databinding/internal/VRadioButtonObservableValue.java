package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Button;

public class VRadioButtonObservableValue extends VAbstractButtonObservableValue {

class ImproperValue {}

Object matchingValue;

public VRadioButtonObservableValue(Button button, Object matchingValue) {
	super(button);
	this.matchingValue = matchingValue;
}

protected boolean isSelectedValue(Object value) {
	return matchingValue.equals(value);
}

protected Object doGetValue() {
	if (!button.getSelection()) 
		return new ImproperValue();
	return matchingValue;
}

public Object getValueType() {
	return matchingValue.getClass();
}

}
