package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

/**
 * If used, property changes that occured on elements are propagated to the UI.
 * 
 * Must not be installed when using a TableViewerEditExtension.
 * 
 * @author maschmid
 *
 */
public class TableViewerElementChangeListenerExtension implements VBindingDomainExtension {

public TableViewerElementChangeListenerExtension() {
}

public void install(VBinding binding) {
	TableController controller = binding.getBindingContext().tableControllerMap.get(getTableViewer(binding));
	controller.install(this);
}

private TableViewer getTableViewer(VBinding binding) {
	return ((VTableViewerObservableList)binding.getBinding().getTarget()).getTableViewer();
}


}
