package org.mariella.glue.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.mariella.glue.service.Entity;
import org.mariella.rcp.databinding.FormPageSelectionExtension;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.forms.AbstractVResourceFormEditor;
import org.mariella.rcp.forms.AbstractVResourceFormEditorCustomizationCallback;
import org.mariella.rcp.resources.AbstractVResourceManager;
import org.mariella.rcp.resources.VResource;
import org.mariella.rcp.resources.VResourceEditorCustomizationCallback;


public abstract class EntityEditor<T extends Entity> extends AbstractVResourceFormEditor {
	public final static String ID = EntityEditor.class.getName();

public class CustomEditingContext {

	public VBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	public EntityAdapter<T> getModel() {
		return EntityEditor.this.getEntityAdapter();
	}
}


class CustomizationCallbackImpl extends AbstractVResourceFormEditorCustomizationCallback {
	private IPartListener partListener = new IPartListener() {
		private ISelection mySelection;
		private boolean needsReload = false;
		
		public void partOpened(IWorkbenchPart part) {
		}
		
		public void partDeactivated(IWorkbenchPart part) {
			if(part == EntityEditor.this) {
				mySelection = getSite().getSelectionProvider().getSelection();
				needsReload = true;
			}
		}
		public void partClosed(IWorkbenchPart part) {
		}
		public void partBroughtToTop(IWorkbenchPart part) {
		}
		public void partActivated(IWorkbenchPart part) {
			if (part == EntityEditor.this && !isDirty() && needsReload) {
				reload();
				needsReload = false;
			}
			getSite().getSelectionProvider().setSelection(
				mySelection == null ? 
					new ISelection() { 
						public boolean isEmpty() {
							return true;
						};
					} :
					mySelection
			);
		}
	};

	public void implementAddPages() {
		EntityEditor.this.implementAddPages();
	}

	public void implementInit() {
		dataBindingContext = getUIRegistration().getDataBindingFactory().createDataBindingContext();
		getEditorSite().setSelectionProvider(dataBindingContext.getDataBindingSelectionProvider());
		getUIRegistration().getDataBindingFactory().addContextExtension(dataBindingContext, new FormPageSelectionExtension(EntityEditor.this));
		getSite().getPage().addPartListener(partListener);
	}

	public void implementDispose() {
		getSite().getPage().removePartListener(partListener);
		dataBindingContext.dispose();
	}


	public VResource getResource() {
		return getEntityAdapter();
	}

	public AbstractVResourceManager getResourceManager() {
		return (AbstractVResourceManager)getUIRegistration().getResourceManager();
	}

	public Object createCustomEditingContext() {
		return new CustomEditingContext();
	}
}

	private VBindingContext dataBindingContext;

public VResourceEditorCustomizationCallback createCustomizationCallback() {
	return new CustomizationCallbackImpl();
}

public VBindingContext getDatabindingContext() {
	return dataBindingContext;
}

@SuppressWarnings("unchecked")
public EntityAdapter<T> getEntityAdapter() {
	return (EntityAdapter<T>)getEditorInput();
}


public UIRegistration<T> getUIRegistration() {
	return getEntityAdapter().getUIRegistration();
}

protected abstract void implementAddPages();


public boolean hasUserEditorAccess() {
	return true;
}

public void reload() {
	BusyIndicator.showWhile(
		getSite().getWorkbenchWindow().getShell().getDisplay(),
		new Runnable() {
			public void run() {
				Activator.logger.info("start refreshing " + EntityEditor.this.getClass().getName());
				getEntityAdapter().reload();
				dataBindingContext.updateTargets();
				Activator.logger.info("end refreshing " + EntityEditor.this.getClass().getName());
			}
		}
	);
}

public boolean makeSaved(String title, String confirmationMessage) {
	if(isDirty()) {
		if(MessageDialog.openConfirm(getSite().getShell(), title, confirmationMessage)) {
			getSite().getPage().saveEditor(this, false);
		}
	};
	return !isDirty();
}

public boolean isReadOnly() {
	return false;
}

}
