package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

public class TableViewerColumnExtension implements BindingDomainExtension {

String propertyPath;
Object domainSymbol;
BindingDomain domain;
String headerText;
int weight;

public TableViewerColumnExtension(String propertyPath, BindingDomain domain, String headerText, int weight) {
	this.propertyPath = propertyPath;
	this.domain = domain;
	this.headerText = headerText;
	this.weight = weight;
}

public TableViewerColumnExtension(String propertyPath, Object domainSymbol, String headerText, int weight) {
	this.propertyPath = propertyPath;
	this.domainSymbol = domainSymbol;
	this.headerText = headerText;
	this.weight = weight;
}

public void install(VBinding binding) {
	TableController controller = binding.getDataBindingContext().tableControllerMap.get(getTableViewer(binding));
	controller.install(this, binding);
}

private TableViewer getTableViewer(VBinding binding) {
	return ((VTableViewerObservableList)binding.getBinding().getTarget()).getTableViewer();
}

public String getHeaderText() {
	return headerText;
}

public int getWeight() {
	return weight;
}

public String getPropertyPath() {
	return propertyPath;
}

public BindingDomain getDomain() {
	return domain;
}

public Object getDomainSymbol() {
	return domainSymbol;
}

public void setDomain(BindingDomain domain) {
	this.domain = domain;
}

}
