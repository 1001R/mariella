package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingContextObserver;

public class PropertyPathObservableList extends ObservableList implements VBindingContextObserver {

private PropertyPathSupport propertyPathSupport = new PropertyPathSupport();
private PropertyListenerSupport propertyListenSupport;
private Object propertyChangeListenerTarget = null;


private IListChangeListener wrappedListChangeListener = new IListChangeListener() {
	public void handleListChange(ListChangeEvent event) {
		fireListChange(event.diff);
	}
};

private IValueChangeListener objectChangeListener = new IValueChangeListener() {
	public void handleValueChange(ValueChangeEvent event) {
		if (!updating) {
			updateWrappedList();
		}
	}
};

private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent event) {
		if (!updating) {
			updateWrappedList();
		}
	}
};

private boolean updating = false;
private VBindingContext bindingContext;


public PropertyPathObservableList(VBindingContext bindingContext, Realm realm, Object object, String propertyPath, Class elementType) {
	super(realm, new ArrayList(), elementType);
	this.bindingContext = bindingContext;
	propertyPathSupport.object = object;
	propertyPathSupport.propertyPath = propertyPath;
	propertyPathSupport.initialize();

	if (object instanceof IObservableValue)
		// for example a SingleSelectionObservableValue 
		((IObservableValue)object).addValueChangeListener(objectChangeListener);
	this.propertyListenSupport = new PropertyListenerSupport(propertyChangeListener, propertyPathSupport.getLastPathComponent());

	updateWrappedList();
	this.bindingContext.addObserver(this);
}

private void updateWrappedList() {
	if (wrappedList instanceof IObservableList) {
		IObservableList observable = (IObservableList)wrappedList;
		observable.removeListChangeListener(wrappedListChangeListener);
	}
	updateWrappedList(getObservedList());
	if (wrappedList instanceof IObservableList) {
		IObservableList observable = (IObservableList)wrappedList;
		observable.addListChangeListener(wrappedListChangeListener);
	}
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
public void dispose() {
	super.dispose();
	lastListenerRemoved();
}


private List getObservedList() {
	List values = primGetValues();
	if (values == null)
		return new ArrayList();
	return values;
}

private List primGetValues() {
	return (List)propertyPathSupport.implementDoGetValue();
}

public Object getObserved() {
	return propertyPathSupport.object;
}

public void aboutToUpdateModelToTarget() {
	updateWrappedList();
}

}
