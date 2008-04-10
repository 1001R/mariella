package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.jface.action.Action;

public class VActionObservable extends AbstractObservableValue implements EnabledObservableValueFactory {

class EnabledObservableValueImpl extends AbstractObservableValue implements EnabledObservableValue {

@Override
protected Object doGetValue() {
	return action.isEnabled();
}
@Override
protected void doSetValue(Object value) {
	action.setEnabled((Boolean)value);
}
public Object getValueType() {
	return Boolean.class;
}
}

Action action;

public VActionObservable(Action action) {
	this.action = action;
}

@Override
protected Object doGetValue() {
	return null;
}

public Object getValueType() {
	return null;
}

public EnabledObservableValue createEnabledObservableValue() {
	return new EnabledObservableValueImpl();
}

}
