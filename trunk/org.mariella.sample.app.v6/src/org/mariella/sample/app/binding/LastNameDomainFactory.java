package org.mariella.sample.app.binding;

import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.PassingConverterBuilder;

public class LastNameDomainFactory extends SampleBindingDomainFactory {

@Override
VBindingDomain createDomain() {
	VBindingDomain domain = new VBindingDomain(
			DomainSymbols.LastName, 
			String.class,
			new PassingConverterBuilder()
		);
	addDefaultTextViewerExtensions(domain);
	return domain;}

}
