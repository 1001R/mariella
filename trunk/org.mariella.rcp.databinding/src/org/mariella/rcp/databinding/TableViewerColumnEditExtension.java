package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

public class TableViewerColumnEditExtension implements BindingDomainExtension {

String propertyPath;
TableViewerColumnEditExtensionCallback callback;

public TableViewerColumnEditExtension(String propertyPath, TableViewerColumnEditExtensionCallback callback) {
	this.propertyPath = propertyPath;
	this.callback = callback;
}

public void install(VBinding binding) {
	TableController controller = binding.getDataBindingContext().tableControllerMap.get(getTableViewer(binding));
	controller.install(this);
}

private TableViewer getTableViewer(VBinding binding) {
	return ((VTableViewerObservableList)binding.getBinding().getTarget()).getTableViewer();
}

public TableViewerColumnEditExtensionCallback getCallback() {
	return callback;
}

public String getPropertyPath() {
	return propertyPath;
}

}
