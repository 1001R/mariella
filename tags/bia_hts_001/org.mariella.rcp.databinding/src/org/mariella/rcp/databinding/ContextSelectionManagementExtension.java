package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.VDataBindingSelectionDispatchContext;
import org.mariella.rcp.databinding.internal.VDataBindingSelectionDispatcher;
import org.mariella.rcp.databinding.internal.VBindingSelectionProvider;

public abstract class ContextSelectionManagementExtension implements DataBindingContextExtension, VDataBindingSelectionDispatcher {

public void dispose() {
}

public void install(VBindingContext bindingContext) {
	((VBindingSelectionProvider)bindingContext.getSelectionProvider()).installContextSelectionManagementExtension(this);
}

public abstract VBindingSelection completeSelectionPath(VBindingSelection selection);

public abstract void dispatchSelection(VDataBindingSelectionDispatchContext dispatchCtx);

}
