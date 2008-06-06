package org.mariella.sample.app.person;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.mariella.rcp.databinding.TextBindingDetails;
import org.mariella.rcp.forms.FormLayoutFactory;
import org.mariella.rcp.forms.VResourceSectionPart;
import org.mariella.sample.app.Activator;
import org.mariella.sample.app.binding.DomainSymbols;
import org.mariella.sample.app.binding.GenderDomainFactory;
import org.mariella.sample.app.person.PersonEditor.CustomEditingContext;

public class PersonEditorSection extends VResourceSectionPart {

public PersonEditorSection(PersonEditorPage page, Composite parent) {
	super(page, parent, page.getManagedForm().getToolkit(), ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
	createClient(getSection());
}

private void createClient(Section section) {
	section.setText("Person Properties");
	section.setDescription("Enter the Person Properties");
	
	Composite client = getVFormToolkit().createComposite(section);
	client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 3));
	section.setClient(client);
	
	addFirstName(client);
	addLastName(client);
	addMaritalStatus(client);
	addGender(client);
	addBuddy(client);
}

private void addFirstName(Composite parent) {
	getVFormToolkit().createLabel(parent, "First Name");
	TextViewer textViewer = getVFormToolkit().createTextViewer(parent, SWT.NONE);
	GridData gridData = new GridData();
	gridData.widthHint = 100;
	gridData.horizontalSpan = 2;
	textViewer.getControl().setLayoutData(gridData);

	Activator.getBindingFactory().createTextBinding(getCustomEditingContext().getBindingContext(), 
			textViewer, 
			getCustomEditingContext().getPersonResource().getPerson(), "firstName", 
			DomainSymbols.FirstName, 
			new TextBindingDetails(SWT.Modify));
}

private void addLastName(Composite parent) {
	getVFormToolkit().createLabel(parent, "Last Name");
	TextViewer textViewer = getVFormToolkit().createTextViewer(parent, SWT.NONE);
	GridData gridData = new GridData();
	gridData.widthHint = 100;
	gridData.horizontalSpan = 2;
	textViewer.getControl().setLayoutData(gridData);

	Activator.getBindingFactory().createTextBinding(getCustomEditingContext().getBindingContext(), 
			textViewer, 
			getCustomEditingContext().getPersonResource().getPerson(), "lastName", 
			DomainSymbols.LastName, 
			new TextBindingDetails(SWT.Modify));
}

private void addBuddy(Composite parent) {
	getVFormToolkit().createLabel(parent, "");
	
	Button checkbox = getVFormToolkit().createButton(parent, "Buddy", SWT.CHECK);
	GridData gridData = new GridData();
	gridData.horizontalSpan = 2;
	checkbox.setLayoutData(gridData);

	Activator.getBindingFactory().createButtonBinding(getCustomEditingContext().getBindingContext(), 
			checkbox, 
			getCustomEditingContext().getPersonResource().getPerson(), "buddy", 
			DomainSymbols.Boolean);
}

private void addGender(Composite parent) {
	getVFormToolkit().createLabel(parent, "");
	
	// we just create a pool of buttons, the other stuff  
	// is done by the VBindingDomain created by the GenderDomainFactory
	Button[] radios = new Button[2];
	for (int i=0; i<GenderDomainFactory.AVAILABLE_VALUES.length; i++)
		radios[i] = getVFormToolkit().createButton(parent, "", SWT.RADIO);
	
	// the pool of buttons is now bound to the Person's gender property:
	Activator.getBindingFactory().createRadioSetBinding(getCustomEditingContext().getBindingContext(), 
			radios, 
			getCustomEditingContext().getPersonResource().getPerson(), "gender", 
			DomainSymbols.Gender);
}

private void addMaritalStatus(Composite parent) {
	getVFormToolkit().createLabel(parent, "Marital Status");
	ComboViewer comboViewer = getVFormToolkit().createComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
	GridData gridData = new GridData();
	gridData.widthHint = 100;
	gridData.horizontalSpan = 2;
	comboViewer.getControl().setLayoutData(gridData);

	Activator.getBindingFactory().createSingleSelectionBinding(getCustomEditingContext().getBindingContext(), 
			comboViewer, 
			getCustomEditingContext().getPersonResource().getPerson(), "maritalStatus", 
			DomainSymbols.MaritalStatus);
}

CustomEditingContext getCustomEditingContext() {
	return (CustomEditingContext)getPage().getCustomEditingContext();
}


}
