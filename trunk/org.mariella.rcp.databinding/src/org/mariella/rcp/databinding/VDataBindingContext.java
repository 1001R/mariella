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
import org.mariella.rcp.databinding.internal.VDataBindingContextObserver;
import org.mariella.rcp.databinding.internal.VDataBindingSelectionProvider;
import org.mariella.rcp.databinding.internal.VTableViewerListBindingImpl;
import org.mariella.rcp.databinding.internal.VTargetObservable;


public class VDataBindingContext {

VDataBindingFactory dataBindingFactory;
public ObservablesManager observablesManager = new ObservablesManager();
private TraverseHandler traverseHandler = new TraverseHandler();
VDataBindingSelectionProvider selectionProvider = new VDataBindingSelectionProvider(this);
private DataBindingContext dataBindingContext;
public Map<ISWTObservable,SWTObservableStatusDecorator> swtObservableStatusDecoratorMap = new HashMap<ISWTObservable, SWTObservableStatusDecorator>();
public Map<TableViewer,TableController> tableControllerMap = new HashMap<TableViewer, TableController>();
private List<VBinding> bindings = new ArrayList<VBinding>();
private List<VDataBindingContextObserver> observers = new ArrayList<VDataBindingContextObserver>();
private IActionBars actionBars;
GlobalClipboardActionsHandler globalClipboardActionsHandler;

VDataBindingContext(VDataBindingFactory dataBindingFactory) {
	this.dataBindingFactory = dataBindingFactory;
	dataBindingContext = new DataBindingContext();
	globalClipboardActionsHandler = new GlobalClipboardActionsHandler(this);
}

public DataBindingContext getDataBindingContext() {
	return dataBindingContext;
}

public void addObserver(VDataBindingContextObserver observer) {
	observers.add(observer);
}

public VBinding bindValue(IObservableValue targetObservableValue,
		IObservableValue modelObservableValue,
		UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget,
		BindingDomain domain) {
	VBinding binding = new VBinding(this, dataBindingContext.bindValue(targetObservableValue, modelObservableValue, targetToModel, modelToTarget),
			domain);
	bindings.add(binding);
	return binding;
}

public VBinding bindList(IObservableList targetObservableList,
		IObservableList modelObservableList,
		UpdateListStrategy targetToModel, UpdateListStrategy modelToTarget,
		BindingDomain domain) {
	VBinding binding = new VBinding(this, createConentViewerListBinding(targetObservableList, modelObservableList, targetToModel, modelToTarget),
			domain);
	bindings.add(binding);
	return binding;
}

private final Binding createConentViewerListBinding(IObservableList targetObservableList,
		IObservableList modelObservableList,
		UpdateListStrategy targetToModel, UpdateListStrategy modelToTarget) {
	ListBinding result = new VTableViewerListBindingImpl(targetObservableList,
			modelObservableList, targetToModel,
			modelToTarget);
	result.init(dataBindingContext);
	return result;
}

public VDataBindingFactory getDataBindingFactory() {
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
	for (VDataBindingContextObserver o : observers)
		o.aboutToUpdateModelToTarget();
	dataBindingContext.updateTargets();
}

public void dispose() {
	globalClipboardActionsHandler.dispose();
	dataBindingContext.dispose();
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
