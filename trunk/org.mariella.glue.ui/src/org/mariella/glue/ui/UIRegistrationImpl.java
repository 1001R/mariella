package org.mariella.glue.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mariella.glue.service.Entity;
import org.mariella.rcp.problems.ProblemManager;
import org.mariella.rcp.resources.VResourceSaveException;
import org.mariella.rcp.resources.VResourcesPlugin;

public abstract class UIRegistrationImpl <T extends Entity> implements UIRegistration<T> {
	private Image image;
	private EntityAdapterResourceManager<T> resourceManager;
	
public UIRegistrationImpl() {
	super();
	resourceManager = new EntityAdapterResourceManager<T>(this);
	VResourcesPlugin.getResourceManagerRegistry().addManager(resourceManager);
}

public abstract String getImageName();

@Override
public String getLabel() {
	int idx = getEntityClass().getName().lastIndexOf('.');
	return idx == -1 ? getEntityClass().getName() : getEntityClass().getName().substring(idx + 1);
}

@Override
public EntityAdapterResourceManager<T> getResourceManager() {
	return resourceManager;
}

public abstract ImageDescriptor getImageDescriptor();

public Image getImage() {
	if(image == null) {
		image = getImageDescriptor().createImage();
	}
	return image;
}

public EntityAdapterResourceManager<T> getVResourceManager() {
	return resourceManager;
}

@Override
public ScreeningProblemScanner createProblemScanner(ProblemManager problemManager, EntityAdapter<T> model) {
	return new ScreeningProblemScanner(problemManager, new ScreeningProblemResource(model.getUIRegistration(), model.getRef(), model.getName()));
}

public void openEditor(final Object identity) {
	BusyIndicator.showWhile(
		Display.getCurrent(),
		new Runnable() {
			public void run() {
				try {
					IEditorInput input = (IEditorInput)getVResourceManager().getModelForIdentity(identity);
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, getEditorId());
				} catch(PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	);
}

public void delete(final Long id) {
	BusyIndicator.showWhile(
			Display.getCurrent(),
			new Runnable() {
				public void run() {
					try {
						getVResourceManager().removeResource(getVResourceManager().getRefForPersistentId(id));
					} catch (VResourceSaveException e) {
						MessageDialog.openError(null, "Cannot delete", e.getCause().getMessage());
					}
				}
			}
	);
}

}
