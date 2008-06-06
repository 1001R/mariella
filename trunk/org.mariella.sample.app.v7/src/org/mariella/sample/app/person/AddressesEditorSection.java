package org.mariella.sample.app.person;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.mariella.rcp.databinding.DomainContextExtension;
import org.mariella.rcp.databinding.EnabledOnSingleSelectionCallback;
import org.mariella.rcp.databinding.EnabledRuleExtension;
import org.mariella.rcp.databinding.SelectionManagementExtension;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.TableViewerColumnEditExtension;
import org.mariella.rcp.databinding.TableViewerColumnEditExtensionCallback;
import org.mariella.rcp.databinding.TableViewerColumnExtension;
import org.mariella.rcp.databinding.TableViewerColumnImageCallback;
import org.mariella.rcp.databinding.TableViewerColumnImageExtension;
import org.mariella.rcp.databinding.TableViewerEditExtension;
import org.mariella.rcp.databinding.TextBindingDetails;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainExtension;
import org.mariella.rcp.databinding.VBindingSelection;
import org.mariella.rcp.forms.FormLayoutFactory;
import org.mariella.rcp.forms.VResourceFormPage;
import org.mariella.rcp.forms.VResourceSectionPart;
import org.mariella.rcp.util.ColumnLayout;
import org.mariella.sample.app.Activator;
import org.mariella.sample.app.binding.CountryByIsoCodeDomainFactory;
import org.mariella.sample.app.binding.DomainSymbols;
import org.mariella.sample.app.person.PersonEditor.CustomEditingContext;
import org.mariella.sample.core.Address;
import org.mariella.sample.core.Country;
import org.mariella.sample.core.SampleCorePlugin;

public class AddressesEditorSection extends VResourceSectionPart {


class NewRowAction extends Action {
public void run() {
	PersonResource personResource = getCustomEditingContext().getPersonResource();
	Address newAddress = personResource.addAddress();
	
	getCustomEditingContext().getBindingContext().updateTargets();

	getPage().getEditor().getSite().getSelectionProvider().setSelection(new VBindingSelection(new SelectionPath(
			getPage().getId(),
			"addresses",
			personResource.getPerson().getAddresses().indexOf(newAddress),
			"zipCode")));
}
}


class RemoveRowAction extends Action {

public void run() {
	PersonResource personResource = getCustomEditingContext().getPersonResource();
	Address selectedAddress = (Address)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
	personResource.removeAddress(selectedAddress);

	getCustomEditingContext().getBindingContext().updateTargets();
}

}


TableViewer tableViewer;
Composite tableComposite;
NewRowAction newRowAction;
RemoveRowAction removeRowAction;

public AddressesEditorSection(VResourceFormPage page, Composite parent) {
	super(page, parent, page.getManagedForm().getToolkit(), ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
	createClient(getSection(), page.getEditor().getToolkit());
}

private void createClient(Section section, FormToolkit toolkit) {
	section.setText("Addresses"); 
	section.setDescription("Specifiy the Addresses of the Person.");
	section.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 1));
	
	Composite client = toolkit.createComposite(section);
	section.setClient(client);
	GridData clientData = new GridData(GridData.FILL_BOTH);
	clientData.grabExcessHorizontalSpace = true;
	client.setLayoutData(clientData);
	client.setLayout(new FormLayout());

	Control table = createTableViewer(client).getTable();
	FormData formData = new FormData();
	formData.left = new FormAttachment(0);
	formData.top= new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(80);
	table.setLayoutData(formData);
	
	Composite buttonComposite = new Composite(client, SWT.NONE);
	formData = new FormData();
	formData.left = new FormAttachment(table, 5);
	formData.top= new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(100);
	buttonComposite.setLayoutData(formData);
	
	buttonComposite.setLayout(new ColumnLayout(0, 2));
	
	addNewRowAction(buttonComposite);
	addRemoveRowAction(buttonComposite);
}

private void addNewRowAction(Composite parent) {
	newRowAction = new NewRowAction();
	Activator.getBindingFactory().createActionBinding(getCustomEditingContext().getBindingContext(),
			getVFormToolkit().createButton(parent, "Add &Address", SWT.PUSH),
			newRowAction
			);
}

private void addRemoveRowAction(Composite buttonComposite) {
	removeRowAction = new RemoveRowAction();
	Activator.getBindingFactory().createActionBinding(getCustomEditingContext().getBindingContext(),
			getVFormToolkit().createButton(buttonComposite, "Remove Address", SWT.PUSH),
			removeRowAction,
			new EnabledRuleExtension(new EnabledOnSingleSelectionCallback(tableViewer))
			);
}

private TableViewer createTableViewer(Composite parent) {
	tableViewer = new TableViewer(getVFormToolkit().createTable(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
	
	Activator.getBindingFactory().createTableViewerListBinding(getCustomEditingContext().getBindingContext(), 
			tableViewer, 
			getCustomEditingContext().getPersonResource().getPerson(), "addresses",		// the property to the java.util.List 
			new VBindingDomain("addresses", 			// create VBindingDomain ...
					Address.class,									// the TableViewer's element type	
					new TableViewerEditExtension(),		// an extension that installs edit behaviour
					
					createZipCodeColumnExtension(),		// adding a "column" extension 
					createZipCodeColumnEditExtension(),	// adding a column edit behaviour
					
					createStreetColumnExtension(),	
					createStreetColumnEditExtension(),

					createCountryColumnExtension(),	
					createCountryColumnEditExtension(),
					createCountryColumnImageExtension(),

					new SelectionManagementExtension("addresses")	// the selection qualifier is "addresses"	
					));
	
	return tableViewer;
}

private VBindingDomainExtension createZipCodeColumnExtension() {
	return new TableViewerColumnExtension("zipCode", DomainSymbols.ZipCode, "Zip", 20);
}

private VBindingDomainExtension createZipCodeColumnEditExtension() {
	return new TableViewerColumnEditExtension("zipCode", new TableViewerColumnEditExtensionCallback() {
		public Control createEditControl(IObservableValue selectionHolder, Composite parent) {
			TextViewer textViewer = new TextViewer(parent, SWT.NONE | SWT.SINGLE);
			
			Activator.getBindingFactory().createTextBinding(getCustomEditingContext().getBindingContext(), 
					textViewer, 
					selectionHolder, "zipCode", 
					Activator.getBindingFactory().copyExtend(DomainSymbols.ZipCode,
							new SelectionManagementExtension("zipCode")		// selection path is relative to the Address bean
							),
					new TextBindingDetails(SWT.Modify));

			return textViewer.getTextWidget();
		}
	});
}

private VBindingDomainExtension createStreetColumnExtension() {
	return new TableViewerColumnExtension("street", DomainSymbols.Street, "Street", 40);
}

private VBindingDomainExtension createStreetColumnEditExtension() {
	return new TableViewerColumnEditExtension("street", new TableViewerColumnEditExtensionCallback() {
		public Control createEditControl(IObservableValue selectionHolder, Composite parent) {
			TextViewer textViewer = new TextViewer(parent, SWT.NONE | SWT.SINGLE);
			
			Activator.getBindingFactory().createTextBinding(getCustomEditingContext().getBindingContext(), 
					textViewer, 
					selectionHolder, "street", 
					Activator.getBindingFactory().copyExtend(DomainSymbols.Street,
							new SelectionManagementExtension("street")		// selection path is relative to the Address bean
							),
					new TextBindingDetails(SWT.Modify));

			return textViewer.getTextWidget();
		}
	});
}

private VBindingDomainExtension createCountryColumnExtension() {
	return new TableViewerColumnExtension("country", DomainSymbols.CountryByIsoCode, "Country", 40);
}

private VBindingDomainExtension createCountryColumnEditExtension() {
	return new TableViewerColumnEditExtension("country", new TableViewerColumnEditExtensionCallback() {
		public Control createEditControl(IObservableValue selectionHolder, Composite parent) {
			TextViewer textViewer = new TextViewer(parent, SWT.NONE | SWT.SINGLE);
			
			Activator.getBindingFactory().createTextBinding(getCustomEditingContext().getBindingContext(), 
					textViewer, 
					selectionHolder, "country", 
					Activator.getBindingFactory().copyExtend(DomainSymbols.CountryByIsoCode,
							new SelectionManagementExtension("country"),		// selection path is relative to the Address bean
							new DomainContextExtension(new CountryByIsoCodeDomainFactory.DefaultContext() {
								public Iterator<Country> getAvailableCountries(String startingWithIsoCode) {
									List<Country> countries = new ArrayList<Country>(SampleCorePlugin.getCoreService().getAvailableCountries());
									countries.add(0, null);
									return countries.iterator();
								}
							})),
					new TextBindingDetails(SWT.Modify));

			return textViewer.getTextWidget();
		}
	});
}

private VBindingDomainExtension createCountryColumnImageExtension() {
	return new TableViewerColumnImageExtension("country", new TableViewerColumnImageCallback() {
		public ImageDescriptor getImageDescriptor(Object element, Object value) {
			if (value == null) return null;
			return Activator.getImageDescriptor("icons/country.png");
		}
	});
}

CustomEditingContext getCustomEditingContext() {
	return (CustomEditingContext)getPage().getCustomEditingContext();
}

}
