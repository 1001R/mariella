package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;

public abstract class CompoundObservableValue extends AbstractObservableValue implements IValueChangeListener {

List<IObservableValue> observableValues;

public CompoundObservableValue(Object bean, String ... propertyPathes) {
	observableValues = new ArrayList<IObservableValue>(propertyPathes.length);
	for (String path : propertyPathes) {
		IObservableValue value = ModelObservables.observeValue(bean, path, Object.class);
		value.addValueChangeListener(this);
		observableValues.add(value);
	}
}

@Override
public synchronized void dispose() {
	super.dispose();
	for (IObservableValue value : observableValues) {
		value.removeValueChangeListener(this);
		value.dispose();
	}
	observableValues = null;
}

public Object getValueType() {
	return Boolean.class;
}

public void handleValueChange(ValueChangeEvent event) {
	revalidate();
}

protected abstract void revalidate();

}
