package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.mariella.rcp.databinding.VDataBindingContext;

public class PropertyPathObservableList extends ObservableList implements VDataBindingContextObserver {

private PropertyPathSupport propertyPathSupport = new PropertyPathSupport();
private PropertyListenerSupport propertyListenSupport;
private Object propertyChangeListenerTarget = null;

private IValueChangeListener objectChangeListener = new IValueChangeListener() {
	public void handleValueChange(ValueChangeEvent event) {
		if (!updating) {
			updateWrappedList(Arrays.asList(getValues()));
			hookPropertyChangeListener();
		}
	}
};
private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
	public void propertyChange(java.beans.PropertyChangeEvent event) {
		if (!updating) {
			updateWrappedList(Arrays.asList(getValues()));
		}
	}
};

private boolean updating = false;


public PropertyPathObservableList(VDataBindingContext ctx, Realm realm, Object object,	String propertyPath, Class elementType) {
	super(realm, new ArrayList(), elementType);
	propertyPathSupport.object = object;
	propertyPathSupport.propertyPath = propertyPath;
	propertyPathSupport.initialize();

	if (object instanceof IObservableValue)
		((IObservableValue)object).addValueChangeListener(objectChangeListener);
	this.propertyListenSupport = new PropertyListenerSupport(propertyChangeListener, propertyPathSupport.getLastPathComponent());

	// initialize list without firing events
	wrappedList.addAll(Arrays.asList(getValues()));
	
	hookPropertyChangeListener();
	ctx.addObserver(this);
}

private void hookPropertyChangeListener() {
	if (propertyChangeListenerTarget != null)
		propertyListenSupport.unhookListener(propertyChangeListenerTarget);
	propertyChangeListenerTarget = propertyPathSupport.readTargetObject();
	if (propertyChangeListenerTarget != null)
		propertyListenSupport.hookListener(propertyChangeListenerTarget);
}

public void dispose() {
	super.dispose();
	lastListenerRemoved();
}


private Object[] getValues() {
	Object[] values = null;

	Object result = primGetValues();
	if (propertyPathSupport.getPropertyType().isArray())
		values = (Object[]) result;
	else {
		// TODO add jUnit for POJO (var. SettableValue) collections
		Collection list = (Collection) result;
		if (list != null) {
			values = list.toArray();
		} else {
			values = new Object[] {};
		}
	}
	return values;
}

private Object primGetValues() {
	return propertyPathSupport.implementDoGetValue();
}

public Object getObserved() {
	return propertyPathSupport.object;
}

private void setValues() {
	Class propertyType = propertyPathSupport.getPropertyType();
	if (propertyType.isArray()) {
		Class componentType = propertyType.getComponentType();
		Object[] newArray = (Object[]) Array.newInstance(componentType,
				wrappedList.size());
		wrappedList.toArray(newArray);
		primSetValues(newArray);
	} else {
		// assume that it is a java.util.List
		primSetValues(new ArrayList(wrappedList));
	}
}

private void primSetValues(Object values) {
	propertyPathSupport.implementDoSetValue(values);
}

public Object set(int index, Object element) {
	getterCalled();
	updating = true;
	try {
		Object oldElement = wrappedList.set(index, element);
		setValues();
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
				index, true, element), Diffs.createListDiffEntry(index + 1,
				false, oldElement)));
		return oldElement;
	} finally {
		updating = false;
	}
}

public Object remove(int index) {
	getterCalled();
	updating = true;
	try {
		Object oldElement = wrappedList.remove(index);
		setValues();
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
				index, false, oldElement)));
		return oldElement;
	} finally {
		updating = false;
	}
}

public boolean add(Object element) {
	updating = true;
	try {
		int index = wrappedList.size();
		boolean result = wrappedList.add(element);
		setValues();
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
				index, true, element)));
		return result;
	} finally {
		updating = false;
	}
}

public void add(int index, Object element) {
	updating = true;
	try {
		wrappedList.add(index, element);
		setValues();
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
				index, true, element)));
	} finally {
		updating = false;
	}
}

public boolean addAll(Collection c) {
	if (c.isEmpty()) {
		return false;
	}
	updating = true;
	try {
		int index = wrappedList.size();
		boolean result = wrappedList.addAll(c);
		setValues();
		ListDiffEntry[] entries = new ListDiffEntry[c.size()];
		int i = 0;
		for (Iterator it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			entries[i++] = Diffs.createListDiffEntry(index++, true, o);
		}
		fireListChange(Diffs.createListDiff(entries));
		return result;
	} finally {
		updating = false;
	}
}

public boolean addAll(int index, Collection c) {
	if (c.isEmpty()) {
		return false;
	}
	updating = true;
	try {
		boolean result = wrappedList.addAll(index, c);
		setValues();
		ListDiffEntry[] entries = new ListDiffEntry[c.size()];
		int i = 0;
		for (Iterator it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			entries[i++] = Diffs.createListDiffEntry(index++, true, o);
		}
		fireListChange(Diffs.createListDiff(entries));
		return result;
	} finally {
		updating = false;
	}
}

public boolean remove(Object o) {
	getterCalled();
	int index = wrappedList.indexOf(o);
	if (index == -1) {
		return false;
	}
	updating = true;
	try {
		Object oldElement = wrappedList.remove(index);
		setValues();
		fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
				index, false, oldElement)));
		return true;
	} finally {
		updating = false;
	}
}

public boolean removeAll(Collection c) {
	getterCalled();
	boolean changed = false;
	updating = true;
	try {
		List diffEntries = new ArrayList();
		for (Iterator it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			int index = wrappedList.indexOf(o);
			if (index != -1) {
				changed = true;
				Object oldElement = wrappedList.remove(index);
				diffEntries.add(Diffs.createListDiffEntry(index, false,
						oldElement));
			}
		}
		setValues();
		fireListChange(Diffs.createListDiff((ListDiffEntry[]) diffEntries
				.toArray(new ListDiffEntry[diffEntries.size()])));
		return changed;
	} finally {
		updating = false;
	}
}

public boolean retainAll(Collection c) {
	getterCalled();
	boolean changed = false;
	updating = true;
	try {
		List diffEntries = new ArrayList();
		int index = 0;
		for (Iterator it = wrappedList.iterator(); it.hasNext();) {
			Object o = it.next();
			boolean retain = c.contains(o);
			if (retain) {
				index++;
			} else {
				changed = true;
				it.remove();
				diffEntries.add(Diffs.createListDiffEntry(index, false, o));
			}
		}
		setValues();
		fireListChange(Diffs.createListDiff((ListDiffEntry[]) diffEntries
				.toArray(new ListDiffEntry[diffEntries.size()])));
		return changed;
	} finally {
		updating = false;
	}
}

public void clear() {
	updating = true;
	try {
		List diffEntries = new ArrayList();
		for (Iterator it = wrappedList.iterator(); it.hasNext();) {
			Object o = it.next();
			diffEntries.add(Diffs.createListDiffEntry(0, false, o));
		}
		setValues();
		fireListChange(Diffs.createListDiff((ListDiffEntry[]) diffEntries
				.toArray(new ListDiffEntry[diffEntries.size()])));
	} finally {
		updating = false;
	}
}

public void aboutToUpdateModelToTarget() {
	updateWrappedList(Arrays.asList(getValues()));
}


}
