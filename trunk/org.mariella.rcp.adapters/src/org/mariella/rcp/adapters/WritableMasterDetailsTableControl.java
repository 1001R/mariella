package org.mariella.rcp.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.mariella.rcp.ControlFactory;
import org.mariella.rcp.databinding.ButtonContributionItem;
import org.mariella.rcp.databinding.CompoundEnabledCallback;
import org.mariella.rcp.databinding.EnabledCallback;
import org.mariella.rcp.databinding.EnabledOnSingleSelectionCallback;
import org.mariella.rcp.databinding.VBindingContext;

public abstract class WritableMasterDetailsTableControl<A extends MasterDetailsAdapter<D>, D extends Object> extends MasterDetailsTableControl<A,D> {

	class AddDetailsAction extends Action {
	@Override
	public void run() {
		D newDetails = getAdapter().addDetails();
		addedDetails(newDetails);
	}
	}
	
	
	class RemoveDetailsAction extends Action {
	@Override
	public void run() {
		getAdapter().removeSelectedDetails();
	}
	}

AddDetailsAction addDetailsAction;
RemoveDetailsAction removeDetailsAction;

public WritableMasterDetailsTableControl(Composite parent, int style, IObservableValue adapterObservable, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style, adapterObservable, controlFactory, bindingContext);
}

public WritableMasterDetailsTableControl(Composite parent, int style, A adapter, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style, adapter, controlFactory, bindingContext);
}

@Override
protected boolean needsButtonComposite() {
	return true;
}

@Override
protected void fillButtonComposite(Composite composite) {
	addAddDetailsAction(composite);
	addRemoveDetailsAction(composite);
}

protected  boolean showAddButton() {
	return true;
}

protected  boolean showRemoveButton() {
	return true;
}

private void addAddDetailsAction(Composite parent) {
	addDetailsAction = new AddDetailsAction();
	if (showAddButton()) {
		new ButtonContributionItem(controlFactory.createButton(parent, getAddDetailsButtonText(), SWT.PUSH), addDetailsAction);
		bindingContext.getBindingFactory().createActionEnabledBinding(bindingContext, addDetailsAction, 
				new CompoundEnabledCallback(createEnabledCallbacks()));

	}
}

private void addRemoveDetailsAction(Composite parent) {
	removeDetailsAction = new RemoveDetailsAction();
	if (showRemoveButton()) {
		new ButtonContributionItem(controlFactory.createButton(parent, getRemoveDetailsButtonText(), SWT.PUSH), removeDetailsAction);
		bindingContext.getBindingFactory().createActionEnabledBinding(bindingContext, removeDetailsAction, 
				new CompoundEnabledCallback(createEnabledCallbacks(new EnabledOnSingleSelectionCallback(tableViewer))));
	}
}

private EnabledCallback[] createEnabledCallbacks(EnabledCallback ... callbacks) {
	List<EnabledCallback> enabledCallbacks = new ArrayList<EnabledCallback>();
	for (EnabledCallback callback : callbacks)
		enabledCallbacks.add(callback);
	addAdditionalEnabledCallbacks(enabledCallbacks);
	return enabledCallbacks.toArray(new EnabledCallback[enabledCallbacks.size()]);
}

protected abstract String getAddDetailsButtonText();

protected abstract String getRemoveDetailsButtonText();

/**
 * Add additional EnabledCallbacks for collection modification actions.
 * 
 * @param callbacks
 */
protected void addAdditionalEnabledCallbacks(List<EnabledCallback> callbacks) {}

}
