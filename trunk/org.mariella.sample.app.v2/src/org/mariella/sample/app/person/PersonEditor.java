package org.mariella.sample.app.person;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mariella.rcp.resources.AbstractVResourceEditor;
import org.mariella.rcp.resources.AbstractVResourceEditorCustomizationCallback;
import org.mariella.rcp.resources.AbstractVResourceManager;
import org.mariella.rcp.resources.VResource;
import org.mariella.rcp.resources.VResourceEditorCustomizationCallback;
import org.mariella.rcp.resources.VResourcesPlugin;

public class PersonEditor extends AbstractVResourceEditor {
public static final String ID = PersonEditor.class.getName();

/**
 * Composite having all the input controls.
 * 
 * @author maschmid
 *
 */
class EditorComposite extends Composite {
private PersonResource resource;
private Text firstNameText;
private Text lastNameText;
public EditorComposite(Composite parent) {
	super(parent, SWT.NONE);
	setLayout(new GridLayout(2, false));
	
	Label label;
	
	label = new Label(this, SWT.NONE);
	label.setText("First Name");
	firstNameText = new Text(this, SWT.BORDER);
	firstNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	firstNameText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			resource.getPerson().setFirstName(firstNameText.getText());
		}
	});

	label = new Label(this, SWT.NONE);
	label.setText("Last Name");
	lastNameText = new Text(this, SWT.BORDER);
	lastNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	lastNameText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			resource.getPerson().setLastName(lastNameText.getText());
		}
	});
}

void refresh(PersonResource resource) {
	this.resource = resource;
	firstNameText.setText(resource.getPerson().getFirstName());
	lastNameText.setText(resource.getPerson().getLastName());
}
}

/**
 * AbstractVResourceEditorCustomizationCallback for customizing the editor 
 * 
 * @author maschmid
 *
 */
class CustomizationCallback extends AbstractVResourceEditorCustomizationCallback {

EditorComposite editorComposite;

@Override
public void implementCreatePartControl(Composite parent) {
	editorComposite = new EditorComposite(parent);
}

@Override
public VResource getResource() {
	return (PersonResource)getEditorInput();
}

@Override
public AbstractVResourceManager getResourceManager() {
	return VResourcesPlugin.getResourceManagerRegistry().getResourceManager(PersonResourceManager.class);
}

@Override
public void implementRefresh(boolean layout) {
	editorComposite.refresh((PersonResource)getResource());
}
}



@Override
public boolean hasUserEditorAccess() {
	return true;
}

@Override
public VResourceEditorCustomizationCallback createCustomizationCallback() {
	return new CustomizationCallback();
}

}
