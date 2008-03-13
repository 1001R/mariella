package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.VDataBindingSelection;
import org.mariella.rcp.databinding.internal.VDataBindingSelectionDispatchContext;
import org.mariella.rcp.databinding.internal.VDataBindingSelectionDispatcher;

public abstract class ContextSelectionManagementExtension implements DataBindingContextExtension, VDataBindingSelectionDispatcher {

public void dispose() {
}

public void install(VDataBindingContext dataBindingContext) {
	dataBindingContext.selectionProvider.installContextSelectionManagementExtension(this);
}

public abstract VDataBindingSelection completeSelectionPath(VDataBindingSelection selection);

public abstract void dispatchSelection(VDataBindingSelectionDispatchContext dispatchCtx);

}
