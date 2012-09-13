package org.mariella.rcp.databinding;

/**
 * Must only be applied to copies of BindingDomains!
 * 
 * @author maschmid
 *
 */
public class DomainContextExtension implements VBindingDomainExtension {

Object domainContext;

public DomainContextExtension(Object domainContext) {
	this.domainContext = domainContext;
}

public void install(VBinding binding) {
	binding.getDomain().setDomainContext(domainContext);
}

}
