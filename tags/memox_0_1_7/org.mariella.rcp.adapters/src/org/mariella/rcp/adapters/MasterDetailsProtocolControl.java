package org.mariella.rcp.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.ControlFactory;
import org.mariella.rcp.databinding.EnabledOnSingleSelectionCallback;
import org.mariella.rcp.databinding.EnabledRuleExtension;
import org.mariella.rcp.databinding.SelectionManagementExtension;
import org.mariella.rcp.databinding.TableViewerElementChangeListenerExtension;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainExtension;
import org.mariella.rcp.util.ColumnLayout;

public abstract class MasterDetailsProtocolControl<A extends MasterDetailsAdapter<K,D>, K extends Object, D extends Object> extends Composite {

	A adapter;
	ControlFactory controlFactory;
	TableViewer protocolViewer;
	
	class RemoveAction extends Action {
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		IStructuredSelection sel = (IStructuredSelection)protocolViewer.getSelection();
		D d = (D)sel.getFirstElement();
		adapter.removeDetails(d);
	}
	}

	class EditAgainAction extends Action {
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		IStructuredSelection sel = (IStructuredSelection)protocolViewer.getSelection();
		D d = (D)sel.getFirstElement();
		adapter.setSelectedDetailsKey(adapter.getDetailsKey(d));
	}
	}
	
	
	EditAgainAction editAgainAction;
	RemoveAction removeAction;

public MasterDetailsProtocolControl(Composite parent, int style, A adapter, ControlFactory controlFactory) {
	super(parent, style);
	this.adapter = adapter;
	this.controlFactory = controlFactory;
	
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
	
	addEditAgainAction(buttonComposite);
	addRemoveRowAction(buttonComposite);
	
	protocolViewer.addDoubleClickListener(new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
			if (editAgainAction.isEnabled())
				editAgainAction.run();
		}
	});
}

private void addEditAgainAction(Composite parent) {
	editAgainAction = new EditAgainAction();
	adapter.getAdapterContext().getBindingContext().getBindingFactory().createActionBinding(adapter.getAdapterContext().getBindingContext(),
			controlFactory.createButton(parent, getCorrectButtonText(), SWT.PUSH), //$NON-NLS-1$
			editAgainAction,
			new EnabledRuleExtension(new EnabledOnSingleSelectionCallback(protocolViewer))
			);
}

private void addRemoveRowAction(Composite parent) {
	removeAction = new RemoveAction();
	adapter.getAdapterContext().getBindingContext().getBindingFactory().createActionBinding(adapter.getAdapterContext().getBindingContext(),
			controlFactory.createButton(parent, getRemoveButtonText(), SWT.PUSH), //$NON-NLS-1$
			removeAction,
			new EnabledRuleExtension(new EnabledOnSingleSelectionCallback(protocolViewer))
			);
}

protected abstract String getCorrectButtonText();

protected abstract String getRemoveButtonText();

private TableViewer createProtocolViewer(Composite parent) {
	protocolViewer = new TableViewer(controlFactory.createTable(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
	
	adapter.getAdapterContext().getBindingContext().getBindingFactory().createTableViewerListBinding(adapter.getAdapterContext().getBindingContext(), 
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
