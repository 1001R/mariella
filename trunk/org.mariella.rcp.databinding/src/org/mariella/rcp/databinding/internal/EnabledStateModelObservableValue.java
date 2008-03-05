package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.mariella.rcp.databinding.EnabledCallback;

public class EnabledStateModelObservableValue extends AbstractObservableValue implements PropertyChangeListener {

Boolean lastEnabled = null;
EnabledCallback enabledCallback;
List<PropertyListenerSupport> listenerSupportList = new ArrayList<PropertyListenerSupport>();
List<Object> listenedBeans = new ArrayList<Object>();

public EnabledStateModelObservableValue(EnabledCallback enabledCallback, Object bean, String ... propertyPathes) {
	this.enabledCallback = enabledCallback;
	for (String path : propertyPathes) {
		PropertyPathSupport ps = new PropertyPathSupport();
		ps.propertyPath = path;
		ps.object = bean;
		ps.initialize();
		Object target = ps.readTargetObject();
		PropertyListenerSupport listenerSupp = new PropertyListenerSupport(this, ps.getLastPathComponent());
		listenerSupp.hookListener(target);
		listenedBeans.add(target);
	}
}

public synchronized void dispose() {
	super.dispose();
	for (int i=0; i<listenerSupportList.size(); i++) {
		PropertyListenerSupport listenerSupp = listenerSupportList.get(i);
		Object target = listenedBeans.get(i);
		listenerSupp.unhookListener(target);
	}
}

protected Object doGetValue() {
	lastEnabled = enabledCallback.isEnabled();
	return lastEnabled;
}

public Object getValueType() {
	return Boolean.class;
}

public void revalidate() {
	fireValueChange(Diffs.createValueDiff(lastEnabled, doGetValue()));
}

public void propertyChange(PropertyChangeEvent evt) {
	revalidate();
}

}
