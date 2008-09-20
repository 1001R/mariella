package org.mariella.glue.annotations.processing;

import org.mariella.glue.annotations.BindingDomain;


public class BindingDomainInfo {
	
	Class<?> declaringClass;
	String attributeName;
	BindingDomain bindingDomain;

public Class<?> getDeclaringClass() {
	return declaringClass;
}

public String getAttributeName() {
	return attributeName;
}

public BindingDomain getBindingDomain() {
	return bindingDomain;
}

@Override
public String toString() {
	return declaringClass.toString() + "#" + attributeName + "=" + bindingDomain.name();
}

}
