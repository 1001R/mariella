package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.mariella.rcp.databinding.FormPageSelectionExtension;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.VDataBindingContext;

public class VDataBindingSelectionProvider implements ISelectionProvider {
private static Log log = LogFactory.getLog(VDataBindingSelectionProvider.class);

@SuppressWarnings("unused")
private VDataBindingContext dataBindingContext;
private ListenerList listeners = new ListenerList();
private List<VBinding> managedBindings = new ArrayList<VBinding>();
FormEditor formEditor = null;
private ISelectionProvider delegateSelectionProvider = null;

public VDataBindingSelectionProvider(VDataBindingContext dbc) {
	this.dataBindingContext = dbc;
}

public ISelection getSelection() {
	if (formEditor != null && formEditor.getActivePageInstance() == null) return new StructuredSelection();
	
	for (VBinding binding : managedBindings) {
		VDataBindingSelection selection = ((SelectionAwareObservable)binding.getBinding().getTarget()).getSelection();
		if (selection != null) {
			if (formEditor == null || formEditor.getActivePageInstance().getId().equals(((SelectionPath)selection.getFirstElement()).getQualifiers()[0]))
				return selection;
		}
	}
	if (delegateSelectionProvider == null) return new StructuredSelection();
	return delegateSelectionProvider.getSelection();
}

public void setSelection(final ISelection selection) {
	if(selection instanceof IStructuredSelection) {
		for (Iterator i= ((IStructuredSelection)selection).iterator(); i.hasNext();) {
			final Object element = i.next();
			if (element instanceof SelectionPath) {
				boolean changedPage = false;
				if (formEditor != null) {
					String newPageId = (String)((SelectionPath)element).getQualifiers()[0];
					changedPage = !formEditor.getActivePageInstance().getId().equals(newPageId);
					if (changedPage)
						formEditor.setActivePage(newPageId);
				}
				Runnable dispatchBlock = new Runnable() {
					public void run() {
						boolean dispatched = false;
						for (VBinding binding : managedBindings) {
							if (((SelectionAwareObservable)binding.getBinding().getTarget()).dispatchSelectionPath((SelectionPath)element, 0)) {
								dispatched = true;
							}
						}
						if (!dispatched) {
							 if (delegateSelectionProvider != null)
								 delegateSelectionProvider.setSelection(selection);
							 else
								 log.warn("Could not dispatch selection having path " + Arrays.toString(((SelectionPath)element).getQualifiers()) + ". If you work with multiple pages, do not forget to add a FormPageSelectionExtension!");
						}
					}
				};
				if (changedPage) 
					Display.getCurrent().asyncExec(dispatchBlock);
				else
					dispatchBlock.run();
			}
		}
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

public void installFormPageSelection(FormPageSelectionExtension formPageSelectionExtension) {
	formEditor = formPageSelectionExtension.getFormEditor();
	
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
