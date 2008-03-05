package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

public class TableViewerColumnImageExtension implements BindingDomainExtension {

String propertyPath;
TableViewerColumnImageCallback imageCallback;

public TableViewerColumnImageExtension(String propertyPath, TableViewerColumnImageCallback imageCallback) {
	this.propertyPath = propertyPath;
	this.imageCallback = imageCallback;
}

public void install(VBinding binding) {
	TableController controller = binding.getDataBindingContext().tableControllerMap.get(getTableViewer(binding));
	controller.install(this);
}

private TableViewer getTableViewer(VBinding binding) {
	return ((VTableViewerObservableList)binding.getBinding().getTarget()).getTableViewer();
}

public String getPropertyPath() {
	return propertyPath;
}

public TableViewerColumnImageCallback getImageCallback() {
	return imageCallback;
}

}
