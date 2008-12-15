package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.mariella.rcp.databinding.internal.InternalBindingContext;
import org.mariella.rcp.databinding.internal.ListViewerController;
import org.mariella.rcp.databinding.internal.VListViewerObservableList;

public class ListViewerImageExtension implements VBindingDomainExtension {

String propertyPath;
ListViewerImageCallback imageCallback;

public ListViewerImageExtension(String propertyPath, ListViewerImageCallback imageCallback) {
	this.propertyPath = propertyPath;
	this.imageCallback = imageCallback;
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

public ListViewerImageCallback getImageCallback() {
	return imageCallback;
}

}
