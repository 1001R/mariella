package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.mariella.rcp.databinding.ContextSelectionManagementExtension;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.VDataBindingContext;
import org.mariella.rcp.databinding.VDataBindingSelection;

public class VDataBindingSelectionProvider implements ISelectionProvider {
@SuppressWarnings("unused")
private static Log log = LogFactory.getLog(VDataBindingSelectionProvider.class);

@SuppressWarnings("unused")
private VDataBindingContext dataBindingContext;
private ListenerList listeners = new ListenerList();
private List<VBinding> managedBindings = new ArrayList<VBinding>();
List<ContextSelectionManagementExtension> contextSelectionManagementExtensions = new ArrayList<ContextSelectionManagementExtension>();
private ISelectionProvider delegateSelectionProvider = null;

public VDataBindingSelectionProvider(VDataBindingContext dbc) {
	this.dataBindingContext = dbc;
}

public ISelection getSelection() {
	for (VBinding binding : managedBindings) {
		// Ask every binding for its selection (if selected). The selection is relative to the context (it does not include for example the page id)
		VDataBindingSelection selection = ((SelectionAwareObservable)binding.getBinding().getTarget()).getSelection();
		if (selection != null) {
			// the target of the binding is selected, let the ContextSelectionManagementExtension complete the selection path...
			List<ContextSelectionManagementExtension> reverseExtension = new ArrayList<ContextSelectionManagementExtension>();
			Collections.reverse(reverseExtension);
			for (ContextSelectionManagementExtension selExt : reverseExtension) {
				selection = selExt.completeSelectionPath(selection);
				if (selection == null)
					return new StructuredSelection();
			}
			return selection;
		}
	}

	if (delegateSelectionProvider == null) return new StructuredSelection();
	return delegateSelectionProvider.getSelection();
}

public void setSelection(final ISelection selection) {
	VDataBindingSelectionDispatchContext dispatchCtx = new VDataBindingSelectionDispatchContext();
	if(selection instanceof VDataBindingSelection) {
		for (SelectionPath path : ((VDataBindingSelection)selection).getSelectionPathes()) {
			List<VDataBindingSelectionDispatcher> allDispatchers = new ArrayList<VDataBindingSelectionDispatcher>();
			allDispatchers.addAll(contextSelectionManagementExtensions);
			for (VBinding binding : managedBindings) {
				if (binding.getBinding().getTarget() instanceof SelectionAwareObservable) {
					VDataBindingSelectionDispatcher dispatcher = ((SelectionAwareObservable)binding.getBinding().getTarget()).getSelectionDispatcher();
					if (dispatcher != null)
						allDispatchers.add(dispatcher);
				}
			}
			
			dispatchCtx.dispatcherChain = allDispatchers.iterator();
			dispatchCtx.selectionPath = path.getQualifiers(); 
			
			dispatchCtx.invokeNextDispatcher(false);
		}
	}
	if (!dispatchCtx.dispatched) {
		 if (delegateSelectionProvider != null)
			 delegateSelectionProvider.setSelection(selection);
	}
}

public void fireSelectionChanged(VDataBindingSelection selection) {
	SelectionChangedEvent ev = new SelectionChangedEvent(this, selection);
	for (Object l : listeners.getListeners())
		((ISelectionChangedListener)l).selectionChanged(ev);
}

public void addSelectionChangedListener(ISelectionChangedListener listener) {
	listeners.add(listener);
	if (delegateSelectionProvider != null)
		delegateSelectionProvider.addSelectionChangedListener(listener);
}

public void removeSelectionChangedListener(ISelectionChangedListener listener) {
	listeners.remove(listener);
	if (delegateSelectionProvider != null)
		delegateSelectionProvider.removeSelectionChangedListener(listener);
}

public void addManagedBinding(VBinding binding) {
	managedBindings.add(binding);
}

public void installContextSelectionManagementExtension(ContextSelectionManagementExtension ext) {
	contextSelectionManagementExtensions.add(ext);
}

public ISelectionProvider getDelegateSelectionProvider() {
	return delegateSelectionProvider;
}

public void setDelegateSelectionProvider(
		ISelectionProvider delegateSelectionProvider) {
	this.delegateSelectionProvider = delegateSelectionProvider;
	for (Object listener : listeners.getListeners())
		delegateSelectionProvider.addSelectionChangedListener((ISelectionChangedListener )listener);
}

}
