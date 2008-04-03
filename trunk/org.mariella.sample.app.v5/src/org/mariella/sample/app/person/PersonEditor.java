package org.mariella.sample.app.person;

import org.eclipse.ui.PartInitException;
import org.mariella.rcp.databinding.VDataBindingContext;
import org.mariella.rcp.forms.AbstractVResourceFormEditor;
import org.mariella.rcp.forms.AbstractVResourceFormEditorCustomizationCallback;
import org.mariella.rcp.resources.VResource;
import org.mariella.rcp.resources.VResourceEditorCustomizationCallback;
import org.mariella.rcp.resources.VResourceManager;
import org.mariella.rcp.resources.VResourcesPlugin;
import org.mariella.sample.app.Activator;

public class PersonEditor extends AbstractVResourceFormEditor {
public static final String ID = PersonEditor.class.getName();

/**
 * Our CustomEditingContext object that is referenced
 * within the different editor sections.
 * 
 * @author maschmid
 *
 */
class CustomEditingContext {

PersonResource getPersonResource() {
	return PersonEditor.this.getPersonResource();
}

VDataBindingContext getDataBindingContext() {
	return dataBindingContext;
}

}


/**
 * AbstractVResourceFormEditorCustomizationCallback for customizing the editor 
 * 
 * @author maschmid
 *
 */
class CustomizationCallback extends AbstractVResourceFormEditorCustomizationCallback {


@Override
public void implementInit() {
	dataBindingContext = Activator.getDataBindingFactory().createDataBindingContext();
}

@Override
public void implementDispose() {
	dataBindingContext.dispose();
}

@Override
public VResource getResource() {
	return (PersonResource)getEditorInput();
}

@Override
public VResourceManager getResourceManager() {
	return VResourcesPlugin.getResourceManagerRegistry().getResourceManager(PersonResourceManager.class);
}

@Override
public void implementAddPages() {
	try {
		addPage(new PersonEditorPage(PersonEditor.this, getPersonResource()));
	} catch (PartInitException e) {
		throw new RuntimeException(e);
	}
}

@Override
public Object createCustomEditingContext() {
	return new CustomEditingContext();
}
}


VDataBindingContext dataBindingContext;


@Override
public VResourceEditorCustomizationCallback createCustomizationCallback() {
	return new CustomizationCallback();
}

PersonResource getPersonResource() {
	return (PersonResource)getEditorInput();
}

}
