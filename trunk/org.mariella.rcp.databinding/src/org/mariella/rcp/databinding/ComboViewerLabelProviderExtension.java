package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.VComboViewerObservableValue;

public class ComboViewerLabelProviderExtension implements BindingDomainExtension {
	
String propertyPath;
Object domainSymbol;
BindingDomain domain;


public ComboViewerLabelProviderExtension(String propertyPath, BindingDomain domain) {
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

public BindingDomain getDomain() {
	return domain;
}

public void setDomain(BindingDomain domain) {
	this.domain = domain;
}
}
