package org.mariella.sample.app.person;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.mariella.rcp.forms.FormLayoutFactory;
import org.mariella.rcp.forms.VResourceSectionPart;
import org.mariella.sample.app.person.PersonEditor.CustomEditingContext;
import org.mariella.sample.core.Person;

public class PersonEditorSection extends VResourceSectionPart {

Text firstNameText;
Text lastNameText;

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
	firstNameText = getVFormToolkit().createText(parent, "", SWT.NONE);
	GridData gridData = new GridData();
	gridData.widthHint = 100;
	firstNameText.setLayoutData(gridData);
	
	final Person person = getCustomEditingContext().getPersonResource().getPerson();
	firstNameText.setText(person.getFirstName() == null ? "" : person.getFirstName());
	firstNameText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			person.setFirstName(firstNameText.getText());
		}
	});
}

private void addLastName(Composite parent) {
	getVFormToolkit().createLabel(parent, "Last Name");
	lastNameText = getVFormToolkit().createText(parent, "", SWT.NONE);
	GridData gridData = new GridData();
	gridData.widthHint = 100;
	lastNameText.setLayoutData(gridData);
	
	final Person person = getCustomEditingContext().getPersonResource().getPerson();
	lastNameText.setText(person.getLastName() == null ? "" : person.getLastName());
	lastNameText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			person.setLastName(lastNameText.getText());
		}
	});
}

CustomEditingContext getCustomEditingContext() {
	return (CustomEditingContext)getPage().getCustomEditingContext();
}


}
