package org.mariella.rcp.databinding.internal;

import java.util.logging.Level;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.mariella.rcp.databinding.AbstractEnabledCallback;
import org.mariella.rcp.databinding.EnabledCallback;
import org.mariella.rcp.databinding.VDataBindingPlugin;

public class EnabledStateModelObservableValue extends CompoundObservableValue implements IValueChangeListener {

Boolean lastEnabled = null;
EnabledCallback enabledCallback;

public EnabledStateModelObservableValue(EnabledCallback enabledCallback, Object bean, String ... propertyPathes) {
	super(bean, propertyPathes);
	this.enabledCallback = enabledCallback;
	if (this.enabledCallback == null) {
		// given enabled callback is null -> interpret values of propertyPathes as the enabled state
		this.enabledCallback = new AbstractEnabledCallback() {
			public boolean isEnabled() {
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
	lastEnabled = enabledCallback.isEnabled();
	return lastEnabled;
}

@Override
public Object getValueType() {
	return Boolean.class;
}

@Override
public void revalidate() {
	fireValueChange(Diffs.createValueDiff(lastEnabled, doGetValue()));
}

@Override
public void handleValueChange(ValueChangeEvent event) {
	revalidate();
}

}
