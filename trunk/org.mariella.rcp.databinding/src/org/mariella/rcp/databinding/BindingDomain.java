package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.validation.IValidator;

public final class BindingDomain {

private Object symbol;
private IValidator[] afterConvertValidators = null;
private ConverterBuilder converterBuilder = null;
private BindingDomainExtension[] extensions = null;
private Class type;
private Object domainContext;

public BindingDomain(BindingDomain domain, BindingDomainExtension ...extensions) {
	this.symbol = domain.symbol;
	this.afterConvertValidators = domain.afterConvertValidators;
	this.converterBuilder = domain.converterBuilder;
	this.extensions = domain.extensions;
	this.type = domain.type;
	addExtensions(extensions);
}

public BindingDomain(Object symbol, Class type, ConverterBuilder converterBuilder) {
	this.symbol = symbol;
	this.type = type;
	this.converterBuilder = converterBuilder;
}

public BindingDomain(Object symbol, Class type) {
	this(symbol, type, new PassingConverterBuilder());
}

public BindingDomain(Object symbol, Class type, BindingDomainExtension ... extensions) {
	this(symbol, type);
	setExtensions(extensions);
}

public BindingDomain(Object symbol, Class type, ConverterBuilder converterBuilder, BindingDomainExtension ... extensions) {
	this(symbol, type);
	this.converterBuilder = converterBuilder;
	setExtensions(extensions);
}


public BindingDomain setAfterConvertValidators(IValidator ...validators) {
	this.afterConvertValidators = validators;
	return this;
}

public BindingDomain setExtensions(BindingDomainExtension ...extensions) {
	this.extensions = extensions;
	return this;
}

public Object getSymbol() {
	return symbol;
}

public IValidator[] getAfterConvertValidators() {
	return afterConvertValidators;
}

public ConverterBuilder getConverterBuilder() {
	return converterBuilder;
}

public BindingDomainExtension[] getExtensions() {
	return extensions;
}

private void addExtensions(BindingDomainExtension ... addExtensions) {
	if (this.extensions == null) {
		this.extensions = addExtensions;
		return;
	}
		
	BindingDomainExtension[] newExts = new BindingDomainExtension[extensions.length + addExtensions.length];
	System.arraycopy(extensions, 0, newExts, 0, extensions.length);
	System.arraycopy(addExtensions, 0, newExts, extensions.length, addExtensions.length);
	this.extensions = newExts;
}

public Class getType() {
	return type;
}

public Object getDomainContext() {
	return domainContext;
}

public void setDomainContext(Object domainContext) {
	this.domainContext = domainContext;
}

public <T> T getExtension(Class<T> clazz) {
	for (BindingDomainExtension ext : extensions)
		if (clazz.isAssignableFrom(ext.getClass()))
			return(T) ext;
	return null;
}

void replaceExtension(EnabledRuleExtension existingEnabledExt, EnabledRuleExtension modifiedEnabledExt) {
	for (int i=0; i<extensions.length; i++) {
		if (extensions[i] == existingEnabledExt)
			extensions[i] = modifiedEnabledExt;
	}
}

}
