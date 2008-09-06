package org.mariella.rcp.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.ControlFactory;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainExtension;
import org.mariella.rcp.util.ColumnLayout;

public abstract class MasterDetailsTableControl <A extends MasterDetailsAdapter<D>, D extends Object> extends Composite {


private A adapter = null;
private IObservableValue adapterObservable = null;


protected ControlFactory controlFactory;
protected TableViewer tableViewer;
VBindingContext bindingContext;	// the binding context of the UI
boolean unmodifiableList = false;

protected void addedDetails(D details) {}

public MasterDetailsTableControl(Composite parent, int style, IObservableValue adapterObservable, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style);
	this.adapterObservable = adapterObservable; 
	this.controlFactory = controlFactory;
	this.bindingContext = bindingContext;
	
	initialize();
}

public MasterDetailsTableControl(Composite parent, int style, A adapter, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style);
	this.adapter = adapter;
	this.controlFactory = controlFactory;
	this.bindingContext = bindingContext;
	
	initialize();
}



private void initialize() {
	
	setLayout(new FormLayout());

	Control table = createTableViewer(this).getTable();
	FormData formData = new FormData();
	formData.left = new FormAttachment(0);
	formData.top= new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(needsButtonComposite() ? 85 : 100);
	table.setLayoutData(formData);
	
	if (needsButtonComposite()) {
		Composite buttonComposite = new Composite(this, SWT.NONE);
		formData = new FormData();
		formData.left = new FormAttachment(table, 5);
		formData.top= new FormAttachment(0);
		formData.bottom = new FormAttachment(100);
		formData.right = new FormAttachment(100);
		buttonComposite.setLayoutData(formData);
		
		buttonComposite.setLayout(new ColumnLayout(0, 2));
		
		fillButtonComposite(buttonComposite);
	}
}

protected boolean needsButtonComposite() {
	return false;
}

protected void fillButtonComposite(Composite composite) {}

private TableViewer createTableViewer(Composite parent) {
	tableViewer = new TableViewer(controlFactory.createTable(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
	
	bindingContext.getBindingFactory().createTableViewerListBinding(bindingContext, 
			tableViewer, 
			getAdapterObserved(), "detailsList",  //$NON-NLS-1$
			new VBindingDomain("details",  //$NON-NLS-1$
					Object.class,
					buildTableBindingDomainExtensions()
					));
	
	bindingContext.getBindingFactory().createSingleSelectionBinding(bindingContext, 
			tableViewer, 
			getAdapterObserved(), "selectedDetails");
	
	decorateTableViewer(tableViewer);
	
	return tableViewer;
}

protected void decorateTableViewer(TableViewer protocolViewer) {}

private VBindingDomainExtension[] buildTableBindingDomainExtensions() {
	List<VBindingDomainExtension> extensions = new ArrayList<VBindingDomainExtension>();
	addTableBindingDomainExtensions(extensions);
	
	VBindingDomainExtension[] array = new VBindingDomainExtension[extensions.size()];
	extensions.toArray(array);
	return array;
}

protected Object getAdapterObserved() {
	if (adapter != null) return adapter;
	return adapterObservable;
}

protected abstract void addTableBindingDomainExtensions(List<VBindingDomainExtension> extensions);

public A getAdapter() {
	if (adapter != null) return adapter;
	return (A)adapterObservable.getValue();
}

}
