package org.mariella.sample.app.person;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.mariella.rcp.resources.AbstractVResourceAction;
import org.mariella.rcp.resources.VResourceSaveException;
import org.mariella.rcp.resources.VResourcesPlugin;

public class DeletePersonAction extends AbstractVResourceAction {
public static final String ID = DeletePersonAction.class.getName();

public DeletePersonAction(IWorkbenchWindow window) {
	super(window, "Delete Person");
	setId(ID);
}

@Override
public void run() {
	PersonResourceManager rm = VResourcesPlugin.getResourceManagerRegistry().getResourceManager(PersonResourceManager.class);
	try {
		rm.removeResource(getSelectedRefHolder().getRef());
	} catch (VResourceSaveException e) {
		MessageDialog.openError(getWindow().getShell(), "Could not delete Person", e.toString());
	}
}

@Override
protected boolean calculateEnabled() {
	return getSelectedRefHolder() instanceof PersonResourceRefHolder;
}


}
