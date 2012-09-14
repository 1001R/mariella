package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.InternalBindingContext;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

public class TableViewerColumnFontExtension implements VBindingDomainExtension {

String propertyPath;
TableViewerColumnFontCallback fontCallback;

public TableViewerColumnFontExtension(String propertyPath, TableViewerColumnFontCallback fontCallback) {
	this.propertyPath = propertyPath;
	this.fontCallback = fontCallback;
}

public void install(VBinding binding) {
	TableController controller = ((InternalBindingContext)binding.getBindingContext()).getMainContext().tableControllerMap.get(getTableViewer(binding));
	controller.install(this);
}

private TableViewer getTableViewer(VBinding binding) {
	return ((VTableViewerObservableList)binding.getBinding().getTarget()).getTableViewer();
}

public String getPropertyPath() {
	return propertyPath;
}

public TableViewerColumnFontCallback getFontCallback() {
	return fontCallback;
}

}