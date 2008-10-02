package org.mariella.rcp.databinding.internal;

import java.util.logging.Level;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.mariella.rcp.databinding.VDataBindingPlugin;
import org.mariella.rcp.databinding.VisibleCallback;

public class VisibleStateModelObservableValue extends CompoundObservableValue implements IValueChangeListener{

Boolean lastVisible = null;
VisibleCallback visibleCallback;

public VisibleStateModelObservableValue(VisibleCallback visibleCallback, Object bean, String ... propertyPathes) {
	super(bean, propertyPathes);
	this.visibleCallback = visibleCallback;
	if (this.visibleCallback == null) {
		// given visible callback is null -> interpret values of propertyPathes as the visible state
		this.visibleCallback = new VisibleCallback() {
			public boolean isVisible() {
				for (IObservableValue obsValue : observableValues) {
					PropertyPathObservableValue pValue = (PropertyPathObservableValue)obsValue;
					Object value = obsValue.getValue();
					if (value != null && !(value instanceof Boolean)) {
						VDataBindingPlugin.logger.log(Level.SEVERE, "bean " + obsValue + "; property " + pValue.getPropertyPath() + " did not evaluate to boolean" );
						return false;
					}
					if (value == null || !((Boolean)value).booleanValue())
						return false;
				}
				return true;
			}
		}; 
	}
}

@Override
protected Object doGetValue() {
	lastVisible = visibleCallback.isVisible();
	return lastVisible;
}

@Override
public Object getValueType() {
	return Boolean.class;
}

@Override
public void revalidate() {
	fireValueChange(Diffs.createValueDiff(lastVisible, doGetValue()));
}

@Override
public void handleValueChange(ValueChangeEvent event) {
	revalidate();
}

}
