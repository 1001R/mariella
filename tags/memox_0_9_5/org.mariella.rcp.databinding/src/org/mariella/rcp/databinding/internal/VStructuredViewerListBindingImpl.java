package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;

import org.eclipse.core.databinding.ListBinding;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.observable.list.IObservableList;

public class VStructuredViewerListBindingImpl extends ListBinding implements BindingDomainExtensionDependendBinding {

boolean extensionsInstalled = false;

public VStructuredViewerListBindingImpl(IObservableList target, IObservableList model, UpdateListStrategy targetToModelStrategy, UpdateListStrategy modelToTargetStrategy) {
	super(target, model, targetToModelStrategy, modelToTargetStrategy);
}

@Override
public void updateModelToTarget() {
	if (!extensionsInstalled) return;

	super.updateModelToTarget();
}

@Override
public void updateTargetToModel() {}

public void extensionsInstalled() {
	extensionsInstalled = true;
	// to resolve every list manipluations into add(...) and remove(...) calls, give the table viewer its seperate list instance
	((VStructuredViewerObservableList)getTarget()).initializePropertyChangeListeners();
	((VStructuredViewerObservableList)getTarget()).setInput(new ArrayList());
	updateModelToTarget();
}

}
