package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.validation.IValidator;

public final class VBindingDomain {

private Object symbol;
private IValidator[] afterConvertValidators = null;
private ConverterBuilder converterBuilder = null;
private VBindingDomainExtension[] extensions = null;
private Class type;
private Object domainContext;

public VBindingDomain(VBindingDomain domain, VBindingDomainExtension ...extensions) {
	this.symbol = domain.symbol;
	this.afterConvertValidators = domain.afterConvertValidators;
	this.converterBuilder = domain.converterBuilder;
	this.extensions = domain.extensions;
	this.type = domain.type;
	addExtensions(extensions);
}

public VBindingDomain(Object symbol, Class type, ConverterBuilder converterBuilder) {
	this.symbol = symbol;
	this.type = type;
	this.converterBuilder = converterBuilder;
}

public VBindingDomain(Object symbol, Class type) {
	this(symbol, type, new PassingConverterBuilder());
}

public VBindingDomain(Object symbol, Class type, VBindingDomainExtension ... extensions) {
	this(symbol, type);
	setExtensions(extensions);
}

public VBindingDomain(Class type, VBindingDomainExtension ... extensions) {
	this("internal", type);
	setExtensions(extensions);
}

public VBindingDomain(Object symbol, Class type, ConverterBuilder converterBuilder, VBindingDomainExtension ... extensions) {
	this(symbol, type);
	this.converterBuilder = converterBuilder;
	setExtensions(extensions);
}


public VBindingDomain setAfterConvertValidators(IValidator ...validators) {
	this.afterConvertValidators = validators;
	return this;
}

public VBindingDomain setExtensions(VBindingDomainExtension ...extensions) {
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

public VBindingDomainExtension[] getExtensions() {
	return extensions;
}

public void addExtensions(VBindingDomainExtension ... addExtensions) {
	if (this.extensions == null) {
		this.extensions = addExtensions;
		return;
	}
		
	VBindingDomainExtension[] newExts = new VBindingDomainExtension[extensions.length + addExtensions.length];
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
	for (VBindingDomainExtension ext : extensions)
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

public VBindingDomain copyExtend(VBindingDomainExtension ... exts) {
	return new VBindingDomain(this, exts);
}

}
