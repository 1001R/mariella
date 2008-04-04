package org.mariella.sample.app.binding;

import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.PassingConverterBuilder;

public class FirstNameDomainFactory extends SampleBindingDomainFactory {

@Override
VBindingDomain createDomain() {
	VBindingDomain domain = new VBindingDomain(
			DomainSymbols.FirstName, 
			String.class,
			new PassingConverterBuilder()
		);
	addDefaultTextViewerExtensions(domain);
	return domain;}

}
