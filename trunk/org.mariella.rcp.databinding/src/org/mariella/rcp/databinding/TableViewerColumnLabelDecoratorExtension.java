package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

public class TableViewerColumnLabelDecoratorExtension implements BindingDomainExtension {

String propertyPath;
TableViewerColumnLabelDecoratorCallback labelDecoratorCallback;

public TableViewerColumnLabelDecoratorExtension(String propertyPath, TableViewerColumnLabelDecoratorCallback labelDecoratorCallback) {
	this.propertyPath = propertyPath;
	this.labelDecoratorCallback = labelDecoratorCallback;
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

public TableViewerColumnLabelDecoratorCallback getLabelDecoratorCallback() {
	return labelDecoratorCallback;
}

}
