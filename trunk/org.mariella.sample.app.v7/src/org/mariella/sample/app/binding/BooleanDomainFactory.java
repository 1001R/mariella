package org.mariella.sample.app.binding;

import org.mariella.rcp.databinding.PassingConverterBuilder;
import org.mariella.rcp.databinding.VBindingDomain;

public class BooleanDomainFactory extends SampleBindingDomainFactory {

@Override
VBindingDomain createDomain() {
	VBindingDomain domain = new VBindingDomain(
			DomainSymbols.Boolean, 
			Boolean.class,
			new PassingConverterBuilder()
		);
	return domain;
}

}
