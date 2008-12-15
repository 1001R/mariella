package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;


public class PropertyPathObservableValue extends AbstractObservableValue {

private PropertyPathSupport propertyPathSupport = new PropertyPathSupport();
private Class valueType;
private PropertyListenerSupport propertyListenSupport;
private boolean updating = false;
private Object propertyChangeListenerTarget = null;

private IValueChangeListener objectChangeListener = new IValueChangeListener() {
	public void handleValueChange(ValueChangeEvent event) {
		Object newValue = doGetValue();
		fireValueChange(Diffs.createValueDiff(new Object(), newValue));
		hookPropertyChangeListener();
	}
};

private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
	public void propertyChange(java.beans.PropertyChangeEvent event) {
		if (!updating) {
			fireValueChange(Diffs.createValueDiff(event.getOldValue(), event.getNewValue()));
		}
	}
};


public PropertyPathObservableValue(Realm realm, Object object, String propertyPath, Class valueType) {
	super(realm);
	propertyPathSupport.object = object;
	propertyPathSupport.propertyPath = propertyPath;
	this.valueType = valueType;
	propertyPathSupport.initialize();
	if (object instanceof IObservableValue)
		((IObservableValue)object).addValueChangeListener(objectChangeListener);
	this.propertyListenSupport = new PropertyListenerSupport(propertyChangeListener, propertyPathSupport.getLastPathComponent());
	hookPropertyChangeListener();
}

private void hookPropertyChangeListener() {
	if (propertyChangeListenerTarget != null)
		propertyListenSupport.unhookListener(propertyChangeListenerTarget);
	propertyChangeListenerTarget = propertyPathSupport.readTargetObject();
	if (propertyChangeListenerTarget != null)
		propertyListenSupport.hookListener(propertyChangeListenerTarget);
}



@Override
public synchronized void dispose() {
	if (propertyPathSupport.object instanceof IObservableValue)
		((IObservableValue)propertyPathSupport.object).removeValueChangeListener(objectChangeListener);
	super.dispose();
}

@Override
protected Object doGetValue() {
	return propertyPathSupport.implementDoGetValue();
}

@Override
protected void doSetValue(Object value) {
	updating = true;
	try {
		Object oldValue = doGetValue();
		propertyPathSupport.implementDoSetValue(value);
		fireValueChange(Diffs.createValueDiff(oldValue, doGetValue()));
	} finally {
		updating = false;
	}
}

public Object getValueType() {
	return valueType;
}

public String getPropertyPath() {
	return propertyPathSupport.propertyPath;
}

}
