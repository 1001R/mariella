package org.mariella.sample.app.person;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.mariella.rcp.forms.FormLayoutFactory;
import org.mariella.rcp.forms.VResourceFormPage;

public class PersonEditorPage extends VResourceFormPage {
public static final String ID = PersonEditorPage.class.getName();

PersonResource personResource;
PersonEditorSection personSection;

public PersonEditorPage(PersonEditor editor, PersonResource personResource) {
	super(editor, ID, "Person");
	this.personResource = personResource;
}

@Override
protected void implementCreateFormContent(IManagedForm managedForm) {
	ScrolledForm scrolledForm = managedForm.getForm();
	FormToolkit toolkit = managedForm.getToolkit();
	toolkit.decorateFormHeading(scrolledForm.getForm());
	scrolledForm.setText("Person");
	
	fillBody(managedForm, toolkit);
}

private void fillBody(IManagedForm managedForm, FormToolkit toolkit) {
	Composite body = managedForm.getForm().getBody();
	body.setLayout(FormLayoutFactory.createFormGridLayout(false, 1));
	
	personSection = new PersonEditorSection(this, body);
	personSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
}

}
