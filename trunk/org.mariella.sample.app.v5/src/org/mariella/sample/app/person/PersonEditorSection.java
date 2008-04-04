package org.mariella.sample.app.person;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.mariella.rcp.databinding.TextDataBindingDetails;
import org.mariella.rcp.forms.FormLayoutFactory;
import org.mariella.rcp.forms.VResourceSectionPart;
import org.mariella.sample.app.Activator;
import org.mariella.sample.app.binding.DomainSymbols;
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
	client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 2));
	section.setClient(client);
	
	addFirstName(client);
	addLastName(client);
}

private void addFirstName(Composite parent) {
	getVFormToolkit().createLabel(parent, "First Name");
	TextViewer textViewer = getVFormToolkit().createTextViewer(parent, SWT.NONE);
	GridData gridData = new GridData();
	gridData.widthHint = 100;
	textViewer.getControl().setLayoutData(gridData);

	Activator.getDataBindingFactory().createTextBinding(getCustomEditingContext().getBindingContext(), 
			textViewer, 
			getCustomEditingContext().getPersonResource().getPerson(), "firstName", 
			DomainSymbols.FirstName, 
			new TextDataBindingDetails(SWT.Modify));
}

private void addLastName(Composite parent) {
	getVFormToolkit().createLabel(parent, "Last Name");
	TextViewer textViewer = getVFormToolkit().createTextViewer(parent, SWT.NONE);
	GridData gridData = new GridData();
	gridData.widthHint = 100;
	textViewer.getControl().setLayoutData(gridData);

	Activator.getDataBindingFactory().createTextBinding(getCustomEditingContext().getBindingContext(), 
			textViewer, 
			getCustomEditingContext().getPersonResource().getPerson(), "lastName", 
			DomainSymbols.LastName, 
			new TextDataBindingDetails(SWT.Modify));
}

CustomEditingContext getCustomEditingContext() {
	return (CustomEditingContext)getPage().getCustomEditingContext();
}


}
