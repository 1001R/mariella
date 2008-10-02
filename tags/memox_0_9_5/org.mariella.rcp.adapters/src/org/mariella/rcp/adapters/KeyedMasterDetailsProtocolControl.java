package org.mariella.rcp.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.mariella.rcp.databinding.TableViewerElementChangeListenerExtension;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainExtension;
import org.mariella.rcp.util.ColumnLayout;

public abstract class KeyedMasterDetailsProtocolControl<A extends KeyedMasterDetailsAdapter<K,D>, K extends Object, D extends Object> extends Composite {

	
	class RemoveAction extends Action {
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		IStructuredSelection sel = (IStructuredSelection)protocolViewer.getSelection();
		D d = (D)sel.getFirstElement();
		adapter.removeDetails(d);
	}
	}
	
	
	A adapter;
	ControlFactory controlFactory;
	TableViewer protocolViewer;
	RemoveAction removeAction;
	VBindingContext bindingContext;	// the binding context of the UI

public KeyedMasterDetailsProtocolControl(Composite parent, int style, A adapter, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style);
	this.adapter = adapter;
	this.controlFactory = controlFactory;
	this.bindingContext = bindingContext;
	
	setLayout(new FormLayout());

	Control table = createProtocolViewer(this).getTable();
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
	
	addRemoveRowAction(buttonComposite);
	
	protocolViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection sel = (IStructuredSelection)protocolViewer.getSelection();
			if (sel.isEmpty()) return;
			D d = (D)sel.getFirstElement();
			KeyedMasterDetailsProtocolControl.this.adapter.setSelectedKey(KeyedMasterDetailsProtocolControl.this.adapter.getKey(d));
		}
	});
}

private void addRemoveRowAction(Composite parent) {
	removeAction = new RemoveAction();
	bindingContext.getBindingFactory().createActionBinding(bindingContext,
			controlFactory.createButton(parent, getRemoveButtonText(), SWT.PUSH), //$NON-NLS-1$
			removeAction,
			createEnabledRuleExtension(new EnabledOnSingleSelectionCallback(protocolViewer))
			);
}

protected EnabledRuleExtension createEnabledRuleExtension(EnabledCallback ...callbacks) {
	List<EnabledCallback> enabledCallbacks = new ArrayList<EnabledCallback>();
	for (EnabledCallback callback : callbacks)
		enabledCallbacks.add(callback);
	addAdditionalEnabledCallbacks(enabledCallbacks);
	EnabledRuleExtension enabledExt = new EnabledRuleExtension(new CompoundEnabledCallback(enabledCallbacks.toArray(new EnabledCallback[enabledCallbacks.size()])));
	return enabledExt;
}

protected void addAdditionalEnabledCallbacks(List<EnabledCallback> callbacks) {}

protected abstract String getRemoveButtonText();

private TableViewer createProtocolViewer(Composite parent) {
	protocolViewer = new TableViewer(controlFactory.createTable(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
	
	bindingContext.getBindingFactory().createTableViewerListBinding(bindingContext, 
			protocolViewer, 
			adapter, "detailsList",  //$NON-NLS-1$
			new VBindingDomain("details",  //$NON-NLS-1$
					Object.class,
					buildTableBindingDomainExtensions()
					));
	
	decorateProtocolViewer(protocolViewer);
	
	return protocolViewer;
}

protected void decorateProtocolViewer(TableViewer protocolViewer) {}

private VBindingDomainExtension[] buildTableBindingDomainExtensions() {
	List<VBindingDomainExtension> extensions = new ArrayList<VBindingDomainExtension>();
	addTableBindingDomainExtensions(extensions);
	
	extensions.add(new SelectionManagementExtension("detailsList")); //$NON-NLS-1$
	extensions.add(new TableViewerElementChangeListenerExtension());
	
	VBindingDomainExtension[] array = new VBindingDomainExtension[extensions.size()];
	extensions.toArray(array);
	return array;
}

protected abstract void addTableBindingDomainExtensions(List<VBindingDomainExtension> extensions);

}
