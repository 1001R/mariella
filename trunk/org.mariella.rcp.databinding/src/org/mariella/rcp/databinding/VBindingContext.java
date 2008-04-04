package org.mariella.rcp.databinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ListBinding;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.mariella.rcp.databinding.internal.GlobalClipboardActionsHandler;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VDataBindingSelectionProvider;
import org.mariella.rcp.databinding.internal.VTableViewerListBindingImpl;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;
import org.mariella.rcp.databinding.internal.VTargetObservable;


public class VBindingContext {

VBindingFactory dataBindingFactory;
public ObservablesManager observablesManager = new ObservablesManager();
private TraverseHandler traverseHandler = new TraverseHandler();
VDataBindingSelectionProvider selectionProvider = new VDataBindingSelectionProvider(this);
DataBindingContext bindingContext;
public Map<ISWTObservable,SWTObservableStatusDecorator> swtObservableStatusDecoratorMap = new HashMap<ISWTObservable, SWTObservableStatusDecorator>();
public Map<TableViewer,TableController> tableControllerMap = new HashMap<TableViewer, TableController>();
private List<VBinding> bindings = new ArrayList<VBinding>();
private List<VBindingContextObserver> observers = new ArrayList<VBindingContextObserver>();
private IActionBars actionBars;
GlobalClipboardActionsHandler globalClipboardActionsHandler;

VBindingContext(VBindingFactory dataBindingFactory) {
	this.dataBindingFactory = dataBindingFactory;
	bindingContext = new DataBindingContext();
	globalClipboardActionsHandler = new GlobalClipboardActionsHandler(this);
}

public DataBindingContext getBindingContext() {
	return bindingContext;
}

public void addObserver(VBindingContextObserver observer) {
	observers.add(observer);
}

public VBinding bindValue(IObservableValue targetObservableValue,
		IObservableValue modelObservableValue,
		UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget,
		VBindingDomain domain) {
	
	return createBinding(
			new Binding[] {bindingContext.bindValue(targetObservableValue, modelObservableValue, targetToModel, modelToTarget)}, 
			domain);
}

VBinding createBinding(Binding[] baseBindings, VBindingDomain domain) {
	VBinding vBinding = new VBinding(this, 
			baseBindings,
			domain);
	this.bindings.add(vBinding);
	return vBinding;
}

public VBinding bindList(IObservableList targetObservableList,
		IObservableList modelObservableList,
		UpdateListStrategy targetToModel, UpdateListStrategy modelToTarget,
		VBindingDomain domain) {
	VBinding binding = new VBinding(this, createListBinding(targetObservableList, modelObservableList, targetToModel, modelToTarget),
			domain);
	bindings.add(binding);
	return binding;
}

private final Binding createListBinding(IObservableList targetObservableList,
		IObservableList modelObservableList,
		UpdateListStrategy targetToModel, UpdateListStrategy modelToTarget) {
	ListBinding result; 	

	if (targetObservableList instanceof VTableViewerObservableList) {
		result = new VTableViewerListBindingImpl(targetObservableList,
				modelObservableList, 
				targetToModel,
				modelToTarget);
	} else {
		result = new ListBinding(targetObservableList, 
				modelObservableList, 
				targetToModel,
				modelToTarget);
	}
	result.init(bindingContext);
	return result;
}

public VBindingFactory getDataBindingFactory() {
	return dataBindingFactory;
}

public TraverseHandler getTraverseHandler() {
	return traverseHandler;
}

public ISelectionProvider getDataBindingSelectionProvider() {
	return selectionProvider;
}

public ISelectionProvider getSelectionProvider() {
	return selectionProvider;
}

public void updateTargets() {
	for (VBindingContextObserver o : observers)
		o.aboutToUpdateModelToTarget();
	bindingContext.updateTargets();
}

public void dispose() {
	globalClipboardActionsHandler.dispose();
	bindingContext.dispose();
	observablesManager.dispose();
	for (VBinding binding : bindings)
		binding.dispose();
}

public void setDelegateSelectionProvider(ISelectionProvider provider) {
	selectionProvider.setDelegateSelectionProvider(provider);
}

public IActionBars getActionBars() {
	return actionBars;
}

public void setActionBars(IActionBars actionBars) {
	this.actionBars = actionBars;
	globalClipboardActionsHandler.initialize(actionBars);
}

public List<VTargetObservable> getObservablesFor(Control control) {
	List<VTargetObservable> result = new ArrayList<VTargetObservable>();
	for (VBinding binding : bindings) {
		IObservable target = binding.getBinding().getTarget();
		if (target instanceof VTargetObservable && ((VTargetObservable)target).isResponsibleFor(control))
			result.add((VTargetObservable)target);
	}
	return result;
}

}
