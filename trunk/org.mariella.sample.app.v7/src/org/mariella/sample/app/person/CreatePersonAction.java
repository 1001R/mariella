package org.mariella.sample.app.person;

import org.eclipse.ui.IWorkbenchWindow;
import org.mariella.rcp.resources.AbstractVResourceAction;
import org.mariella.rcp.resources.VResourcesPlugin;

public class CreatePersonAction extends AbstractVResourceAction {
public static final String ID = CreatePersonAction.class.getName();

public CreatePersonAction(IWorkbenchWindow window) {
	super(window, "Create Person");
	setId(ID);
}

@Override
public void run() {
	PersonResourceManager rm = VResourcesPlugin.getResourceManagerRegistry().getResourceManager(PersonResourceManager.class);
	PersonResource newPersonResource = (PersonResource)rm.createNewResource();
	rm.openEditor(getWindow(), newPersonResource);
	newPersonResource.fireChanged();
}

@Override
protected boolean calculateEnabled() {
	return true;
}


}
