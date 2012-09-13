package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.mariella.rcp.databinding.internal.InternalBindingContext;
import org.mariella.rcp.databinding.internal.ListViewerController;
import org.mariella.rcp.databinding.internal.VListViewerObservableList;

public class ListViewerFontExtension implements VBindingDomainExtension {

String propertyPath;
ListViewerFontCallback fontCallback;

public ListViewerFontExtension(String propertyPath, ListViewerFontCallback fontCallback) {
	this.propertyPath = propertyPath;
	this.fontCallback = fontCallback;
}

public void install(VBinding binding) {
	ListViewerController controller = ((InternalBindingContext)binding.getBindingContext()).getMainContext().listViewerControllerMap.get(getListViewer(binding));
	controller.install(this);
}

private AbstractListViewer getListViewer(VBinding binding) {
	return ((VListViewerObservableList)binding.getBinding().getTarget()).getListViewer();
}

public String getPropertyPath() {
	return propertyPath;
}

public ListViewerFontCallback getFontCallback() {
	return fontCallback;
}

}
