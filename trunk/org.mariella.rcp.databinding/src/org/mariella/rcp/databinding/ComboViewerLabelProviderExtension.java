package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.VComboViewerObservableValue;

public class ComboViewerLabelProviderExtension implements VBindingDomainExtension {
	
String propertyPath;
Object domainSymbol;
VBindingDomain domain;


public ComboViewerLabelProviderExtension(String propertyPath, VBindingDomain domain) {
	this.propertyPath = propertyPath;
	this.domain = domain;
}

public ComboViewerLabelProviderExtension(String propertyPath, Object domainSymbol) {
	this.propertyPath = propertyPath;
	this.domainSymbol = domainSymbol;
}

public void install(VBinding binding) {
	((VComboViewerObservableValue)binding.getBinding().getTarget()).installLabelProviderExtension(this, binding);

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
