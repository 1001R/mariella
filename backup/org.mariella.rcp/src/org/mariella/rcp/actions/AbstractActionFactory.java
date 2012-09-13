package org.mariella.rcp.actions;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public abstract class AbstractActionFactory {

private String actionId;

protected AbstractActionFactory(String actionId) {
    this.actionId = actionId;
}

public final IWorkbenchAction create(IWorkbenchWindow window, ActionFactoryCallback callback) {
	IWorkbenchAction action = implementCreate(window);
	if (callback != null) callback.createdAction(action);
	return action;
}

protected abstract IWorkbenchAction implementCreate(IWorkbenchWindow window);

public IWorkbenchAction create(IWorkbenchWindow window) {
	return create(window, null);
}

public String getId() {
	return actionId;
}


}
