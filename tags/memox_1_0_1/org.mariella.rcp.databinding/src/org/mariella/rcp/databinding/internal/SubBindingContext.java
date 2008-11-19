package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;
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

public VBinding bindValue(IObservableValue targetObservableValue, IObservableValue modelObservableValue, UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget, VBindingDomain domain) {
	VBinding binding = mainBindingContext.bindValue(targetObservableValue, modelObservableValue, targetToModel, modelToTarget, domain);
	bindings.add(binding);
	return binding;
}

public VBinding createBinding(Binding[] baseBindings, VBindingDomain domain) {
	VBinding binding = mainBindingContext.createBinding(baseBindings, domain);
	bindings.add(binding);
	return binding;
}

public MainBindingContext getMainContext() {
	return mainBindingContext;
}

public void addObserver(VBindingContextObserver observer) {
	observers.add(observer);
	mainBindingContext.addObserver(observer);
}

public void dispose() {
	throw new UnsupportedOperationException("Not to use for sub binding contexts.");
}

public VBindingFactory getBindingFactory() {
	return mainBindingContext.getBindingFactory();
}

public ISelectionProvider getDataBindingSelectionProvider() {
	return mainBindingContext.getDataBindingSelectionProvider();
}

public ISelectionProvider getSelectionProvider() {
	return mainBindingContext.getSelectionProvider();
}

public void setDelegateSelectionProvider(ISelectionProvider delegateProvider) {
	throw new UnsupportedOperationException("Not to use for sub binding contexts.");
}

public void updateTargets() {
	for (VBindingContextObserver o : observers)
		o.aboutToUpdateModelToTarget();
	for (VBinding binding : bindings)
		binding.getBinding().updateModelToTarget();
	for (VBindingContextObserver o : observers)
		o.finishedUpdateModelToTarget();

}

public VBindingContext createSubBindingContext() {
	throw new UnsupportedOperationException("Currently not to use for sub binding contexts.");
}

public void setActionBars(IActionBars actionBars) {
	throw new UnsupportedOperationException("Currently not to use for sub binding contexts.");
}

}
