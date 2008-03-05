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

public synchronized void addChangeListener(IChangeListener listener) {
	nested.addChangeListener(listener);
}

public synchronized void removeChangeListener(IChangeListener listener) {
	nested.removeChangeListener(listener);
}

public synchronized void addStaleListener(IStaleListener listener) {
	nested.addStaleListener(listener);
}

public synchronized void removeStaleListener(IStaleListener listener) {
	nested.removeStaleListener(listener);
}

public synchronized void addValueChangeListener(IValueChangeListener listener) {
	nested.addValueChangeListener(listener);
}

public synchronized void removeValueChangeListener(IValueChangeListener listener) {
	nested.removeValueChangeListener(listener);
}

public boolean isStale() {
	return nested.isStale();
}

public synchronized void dispose() {
	super.dispose();
	nested.dispose();
}

protected Object doGetValue() {
	return nested.getValue();
}

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
