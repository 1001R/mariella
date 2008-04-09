package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;

public abstract class CompoundObservableValue extends AbstractObservableValue implements IValueChangeListener {
static Log log = LogFactory.getLog(CompoundObservableValue.class);

List<IObservableValue> observableValues;

public CompoundObservableValue(Object bean, String ... propertyPathes) {
	observableValues = new ArrayList<IObservableValue>(propertyPathes.length);
	for (String path : propertyPathes) {
		IObservableValue value = ModelObservables.observeValue(bean, path, Object.class);
		value.addValueChangeListener(this);
		observableValues.add(value);
	}
}

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
