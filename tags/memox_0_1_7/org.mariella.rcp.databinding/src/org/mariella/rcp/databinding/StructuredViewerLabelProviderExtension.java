package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.mariella.rcp.databinding.internal.PropertyPathSupport;
import org.mariella.rcp.databinding.internal.VStructuredViewerSelectionObservableValue;

public class StructuredViewerLabelProviderExtension implements VBindingDomainExtension {

String propertyPath;
Object domainSymbol = null;
VBindingDomain domain = null;
IConverter converter = null;


public StructuredViewerLabelProviderExtension(String propertyPath, VBindingDomain domain) {
    this.propertyPath = propertyPath;
    this.domain = domain;
}

public StructuredViewerLabelProviderExtension(String propertyPath, Object domainSymbol) {
    this.propertyPath = propertyPath;
    this.domainSymbol = domainSymbol;
}

public void install(VBinding binding) {
	if (domain == null)
		domain = binding.getBindingContext().getBindingFactory().getDomainRegistry().getDomain(domainSymbol);
	converter = domain.getConverterBuilder().buildFromModelConverter(domain); 
	((VStructuredViewerSelectionObservableValue)binding.getBinding().getTarget()).getStructuredViewer().setLabelProvider(new ILabelProvider() {
		@Override
		public void removeListener(ILabelProviderListener listener) {}
	
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
	
		@Override
		public void dispose() {}
	
		@Override
		public void addListener(ILabelProviderListener listener) {}
	
		@Override
		public String getText(Object element) {
			PropertyPathSupport pathSup = new PropertyPathSupport();
			pathSup.object = element;
			pathSup.propertyPath = propertyPath;
			pathSup.initialize();
			Object value = pathSup.implementDoGetValue();
			return (String) converter.convert(value);
		}
	
		@Override
		public Image getImage(Object element) {
			return null;
		}
	
	});

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
