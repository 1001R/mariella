package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingContextObserver;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingFactory;

public class SubBindingContext implements InternalBindingContext {

private MainBindingContext mainBindingContext;
private List<VBinding> bindings = new ArrayList<VBinding>();
private List<VBindingContextObserver> observers = new ArrayList<VBindingContextObserver>();

public SubBindingContext(MainBindingContext mainBindingContext) {
	this.mainBindingContext = mainBindingContext;
}

@Override
public VBinding bindValue(IObservableValue targetObservableValue, IObservableValue modelObservableValue, UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget, VBindingDomain domain) {
	VBinding binding = mainBindingContext.bindValue(targetObservableValue, modelObservableValue, targetToModel, modelToTarget, domain);
	bindings.add(binding);
	return binding;
}

@Override
public VBinding createBinding(Binding[] baseBindings, VBindingDomain domain) {
	VBinding binding = mainBindingContext.createBinding(baseBindings, domain);
	bindings.add(binding);
	return binding;
}

@Override
public MainBindingContext getMainContext() {
	return mainBindingContext;
}

@Override
public void addObserver(VBindingContextObserver observer) {
	observers.add(observer);
	mainBindingContext.addObserver(observer);
}

@Override
public void dispose() {
	throw new UnsupportedOperationException("Not to use for sub binding contexts.");
}

@Override
public VBindingFactory getBindingFactory() {
	return mainBindingContext.getBindingFactory();
}

@Override
public ISelectionProvider getDataBindingSelectionProvider() {
	return mainBindingContext.getDataBindingSelectionProvider();
}

@Override
public ISelectionProvider getSelectionProvider() {
	return mainBindingContext.getSelectionProvider();
}

@Override
public void setDelegateSelectionProvider(ISelectionProvider delegateProvider) {
	throw new UnsupportedOperationException("Not to use for sub binding contexts.");
}

@Override
public void updateTargets() {
	for (VBindingContextObserver o : observers)
		o.aboutToUpdateModelToTarget();
	for (Iterator it = bindings.iterator(); it.hasNext();) {
		VBinding binding = (VBinding) it.next();
		binding.getBinding().updateModelToTarget();
	}

}

@Override
public VBindingContext createSubBindingContext() {
	throw new UnsupportedOperationException("Currently not to use for sub binding contexts.");
}

}
