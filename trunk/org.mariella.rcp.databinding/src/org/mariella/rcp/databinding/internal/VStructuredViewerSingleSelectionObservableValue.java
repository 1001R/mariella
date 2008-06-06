package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.databinding.ValueSetExtension;

public class VStructuredViewerSingleSelectionObservableValue extends AbstractObservableValue implements ValueSetAwareObservable, VTargetObservable {

private IObservableValue nested;
private StructuredViewer structuredViewer;
Class targetType = null;
Object value;

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
	this.value = value;
	nested.setValue(value);
}

public Object getValueType() {
	return nested.getValueType();
}

public StructuredViewer getStructuredViewer() {
	return structuredViewer;
}

@Override
public void installValueSetExtension(final ValueSetExtension ext) {
	structuredViewer.setContentProvider(new IStructuredContentProvider() {
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	
		@Override
		public void dispose() {}

		@Override
		public Object[] getElements(Object inputElement) {
			return ext.getValueSet().toArray();
		}
	
	});
	structuredViewer.setInput(ext.getValueSet());

}

@Override
public boolean blockDefaultTraversing() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void extensionsInstalled() {
	doSetValue(value);
}

@Override
public boolean isResponsibleFor(Control control) {
	return structuredViewer.getControl() == control;
}

}
