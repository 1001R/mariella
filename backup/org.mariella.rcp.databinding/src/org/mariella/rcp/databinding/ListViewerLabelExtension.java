package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.mariella.rcp.databinding.internal.InternalBindingContext;
import org.mariella.rcp.databinding.internal.ListViewerController;
import org.mariella.rcp.databinding.internal.VListViewerObservableList;

public class ListViewerLabelExtension implements VBindingDomainExtension {

String propertyPath;
Object domainSymbol;
VBindingDomain domain;

public ListViewerLabelExtension(String propertyPath, VBindingDomain domain) {
	this.propertyPath = propertyPath;
	this.domain = domain;
}

public ListViewerLabelExtension(String propertyPath, Object domainSymbol) {
	this.propertyPath = propertyPath;
	this.domainSymbol = domainSymbol;
}

public void install(VBinding binding) {
	ListViewerController controller = ((InternalBindingContext)binding.getBindingContext()).getMainContext().listViewerControllerMap.get(getListViewer(binding));
	controller.install(this, binding);
}

private AbstractListViewer getListViewer(VBinding binding) {
	return ((VListViewerObservableList)binding.getBinding().getTarget()).getListViewer();
}

public String getPropertyPath() {
	return propertyPath;
}

public Object getDomainSymbol() {
	return domainSymbol;
}

public VBindingDomain getDomain() {
	return domain;
}

public void setDomain(VBindingDomain domain) {
	this.domain = domain;
}

}
