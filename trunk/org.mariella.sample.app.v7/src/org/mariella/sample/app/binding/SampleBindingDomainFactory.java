package org.mariella.sample.app.binding;

import java.util.ArrayList;
import java.util.List;

import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainRegistry;
import org.mariella.rcp.databinding.TextViewerFocusBehaviourExtension;
import org.mariella.rcp.databinding.VBindingFactory;

public abstract class SampleBindingDomainFactory {

private static List<SampleBindingDomainFactory> factories = new ArrayList<SampleBindingDomainFactory>();

static {
	factories.add(new FirstNameDomainFactory());
	factories.add(new LastNameDomainFactory());
	factories.add(new GenderDomainFactory());
	factories.add(new MaritalStatusDomainFactory());
	factories.add(new MaritalStatusAsTextDomainFactory());
	factories.add(new BooleanDomainFactory());
	factories.add(new StreetDomainFactory());
	factories.add(new ZipCodeDomainFactory());
	factories.add(new CountryByIsoCodeDomainFactory());
}

abstract VBindingDomain createDomain();

public static void addDefaultTextViewerExtensions(VBindingDomain domain) {
	domain.addExtensions(new TextViewerFocusBehaviourExtension());
}

public static VBindingFactory buildBindingFactory() {
	VBindingDomainRegistry domainReg = new VBindingDomainRegistry();
	
	for (SampleBindingDomainFactory f : factories)
		domainReg.addDomain(f.createDomain());
	
	return new VBindingFactory(domainReg);
}


}
