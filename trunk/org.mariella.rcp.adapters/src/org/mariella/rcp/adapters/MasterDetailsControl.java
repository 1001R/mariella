package org.mariella.rcp.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.ControlFactory;
import org.mariella.rcp.databinding.CompoundEnabledCallback;
import org.mariella.rcp.databinding.EnabledCallback;
import org.mariella.rcp.databinding.EnabledOnSingleSelectionCallback;
import org.mariella.rcp.databinding.EnabledRuleExtension;
import org.mariella.rcp.databinding.SelectionManagementExtension;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainExtension;
import org.mariella.rcp.util.ColumnLayout;

public abstract class MasterDetailsControl <A extends MasterDetailsAdapter<D>, D extends Object> extends Composite {

	class AddDetailsAction extends Action {
	@Override
	public void run() {
		D newDetails = adapter.addDetails();
		addedDetails(newDetails);
	}
	}
	
	
	class RemoveDetailsAction extends Action {
	@Override
	public void run() {
		adapter.removeSelectedDetails();
	}
	}

protected A adapter;
protected ControlFactory controlFactory;
protected TableViewer tableViewer;
AddDetailsAction addDetailsAction;
RemoveDetailsAction removeDetailsAction;
VBindingContext bindingContext;	// the binding context of the UI

protected void addedDetails(D details) {}

public MasterDetailsControl(Composite parent, int style, A adapter, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style);
	this.adapter = adapter;
	this.controlFactory = controlFactory;
	this.bindingContext = bindingContext;
	
	setLayout(new FormLayout());

	Control table = createTableViewer(this).getTable();
	FormData formData = new FormData();
	formData.left = new FormAttachment(0);
	formData.top= new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(85);
	table.setLayoutData(formData);
	
	Composite buttonComposite = new Composite(this, SWT.NONE);
	formData = new FormData();
	formData.left = new FormAttachment(table, 5);
	formData.top= new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(100);
	buttonComposite.setLayoutData(formData);
	
	buttonComposite.setLayout(new ColumnLayout(0, 2));
	
	addAddDetailsAction(buttonComposite);
	addRemoveDetailsAction(buttonComposite);
}

private void addAddDetailsAction(Composite parent) {
	addDetailsAction = new AddDetailsAction();
	bindingContext.getBindingFactory().createActionBinding(bindingContext,
			controlFactory.createButton(parent, getAddDetailsButtonText(), SWT.PUSH), //$NON-NLS-1$
			addDetailsAction,
			createEnabledRuleExtension()
			);
}

private void addRemoveDetailsAction(Composite parent) {
	removeDetailsAction = new RemoveDetailsAction();
	bindingContext.getBindingFactory().createActionBinding(bindingContext,
			controlFactory.createButton(parent, getRemoveDetailsButtonText(), SWT.PUSH), //$NON-NLS-1$
			removeDetailsAction,
			createEnabledRuleExtension(new EnabledOnSingleSelectionCallback(tableViewer))
			);
}

protected EnabledRuleExtension createEnabledRuleExtension(EnabledCallback ... callbacks) {
	List<EnabledCallback> enabledCallbacks = new ArrayList<EnabledCallback>();
	for (EnabledCallback callback : callbacks)
		enabledCallbacks.add(callback);
	addAdditionalEnabledCallbacks(enabledCallbacks);
	EnabledRuleExtension enabledExt = new EnabledRuleExtension(new CompoundEnabledCallback(enabledCallbacks.toArray(new EnabledCallback[enabledCallbacks.size()])));
	return enabledExt;
}

protected void addAdditionalEnabledCallbacks(List<EnabledCallback> callbacks) {}

protected abstract String getAddDetailsButtonText();

protected abstract String getRemoveDetailsButtonText();

private TableViewer createTableViewer(Composite parent) {
	tableViewer = new TableViewer(controlFactory.createTable(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
	
	bindingContext.getBindingFactory().createTableViewerListBinding(bindingContext, 
			tableViewer, 
			adapter, "detailsList",  //$NON-NLS-1$
			new VBindingDomain("details",  //$NON-NLS-1$
					Object.class,
					buildTableBindingDomainExtensions()
					));
	
	bindingContext.getBindingFactory().createSingleSelectionBinding(bindingContext, 
			tableViewer, 
			adapter, "selectedDetails");
	
	decorateTableViewer(tableViewer);
	
	return tableViewer;
}

protected void decorateTableViewer(TableViewer protocolViewer) {}

private VBindingDomainExtension[] buildTableBindingDomainExtensions() {
	List<VBindingDomainExtension> extensions = new ArrayList<VBindingDomainExtension>();
	addTableBindingDomainExtensions(extensions);
	
	extensions.add(new SelectionManagementExtension("detailsList")); //$NON-NLS-1$
	extensions.add(createEnabledRuleExtension());
	
	VBindingDomainExtension[] array = new VBindingDomainExtension[extensions.size()];
	extensions.toArray(array);
	return array;
}

protected abstract void addTableBindingDomainExtensions(List<VBindingDomainExtension> extensions);

}
