package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.mariella.rcp.databinding.VisibleCallback;

public class VisibleStateModelObservableValue extends AbstractObservableValue implements PropertyChangeListener {
static Log log = LogFactory.getLog(VisibleStateModelObservableValue.class); 

Boolean lastVisible = null;
VisibleCallback visibleCallback;
List<PropertyListenerSupport> listenerSupportList = new ArrayList<PropertyListenerSupport>();
List<Object> listenedBeans = new ArrayList<Object>();
List<PropertyPathSupport> propertyPathSupporters;

public VisibleStateModelObservableValue(VisibleCallback visibleCallback, Object bean, String ... propertyPathes) {
	this.visibleCallback = visibleCallback;
	propertyPathSupporters = new ArrayList<PropertyPathSupport>(propertyPathes.length);
	for (String path : propertyPathes) {
		PropertyPathSupport ps = new PropertyPathSupport();
		ps.propertyPath = path;
		ps.object = bean;
		ps.initialize();
		propertyPathSupporters.add(ps);
	}
	
	if (this.visibleCallback == null) {
		// given visible callback is null -> interpret values of propertyPathes as the visible state
		this.visibleCallback = new VisibleCallback() {
			public boolean isVisible() {
				for (PropertyPathSupport ps : propertyPathSupporters) {
					Object value = ps.implementDoGetValue();
					if (value != null && !(value instanceof Boolean)) {
						log.error("bean " + ps.object + "; property " + ps.propertyPath + " did not evaluate to boolean" );
						return false;
					}
					if (value == null || !((Boolean)value).booleanValue())
						return false;
				}
				return true;
			}
		}; 
	}
	for (PropertyPathSupport ps : propertyPathSupporters) {
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
	lastVisible = visibleCallback.isVisible();
	return lastVisible;
}

public Object getValueType() {
	return Boolean.class;
}

public void revalidate() {
	fireValueChange(Diffs.createValueDiff(lastVisible, doGetValue()));
}

public void propertyChange(PropertyChangeEvent evt) {
	revalidate();
}

}
