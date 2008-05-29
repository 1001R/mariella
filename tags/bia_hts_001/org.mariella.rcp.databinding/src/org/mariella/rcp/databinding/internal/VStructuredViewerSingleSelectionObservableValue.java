package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.jface.viewers.StructuredViewer;

public class VStructuredViewerSingleSelectionObservableValue extends AbstractObservableValue {

private IObservableValue nested;
private StructuredViewer structuredViewer;
Class targetType = null;

public VStructuredViewerSingleSelectionObservableValue(IObservableValue nested, StructuredViewer structedViewer) {
	this.nested = nested;
	this.structuredViewer = structedViewer;
}

public VStructuredViewerSingleSelectionObservableValue(IObservableValue nested, StructuredViewer structedViewer, Class targetType) {
	this.nested = nested;
	this.structuredViewer = structedViewer;
	this.targetType = targetType;
}

@Override
public synchronized void addChangeListener(IChangeListener listener) {
	nested.addChangeListener(listener);
}

@Override
public synchronized void removeChangeListener(IChangeListener listener) {
	nested.removeChangeListener(listener);
}

@Override
public synchronized void addStaleListener(IStaleListener listener) {
	nested.addStaleListener(listener);
}

@Override
public synchronized void removeStaleListener(IStaleListener listener) {
	nested.removeStaleListener(listener);
}

@Override
public synchronized void addValueChangeListener(IValueChangeListener listener) {
	nested.addValueChangeListener(listener);
}

@Override
public synchronized void removeValueChangeListener(IValueChangeListener listener) {
	nested.removeValueChangeListener(listener);
}

@Override
public boolean isStale() {
	return nested.isStale();
}

@Override
public synchronized void dispose() {
	super.dispose();
	nested.dispose();
}

@Override
protected Object doGetValue() {
	return nested.getValue();
}

@Override
protected void doSetValue(Object value) {
	nested.setValue(value);
}

public Object getValueType() {
	return nested.getValueType();
}

public StructuredViewer getStructuredViewer() {
	return structuredViewer;
}

}
