package org.mariella.sample.app.binding;

import java.util.ArrayList;
import java.util.List;

import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainRegistry;
import org.mariella.rcp.databinding.TextViewerFocusBehaviourExtension;
import org.mariella.rcp.databinding.VDataBindingFactory;

public abstract class SampleBindingDomainFactory {

private static List<SampleBindingDomainFactory> factories = new ArrayList<SampleBindingDomainFactory>();

static {
	factories.add(new FirstNameDomainFactory());
	factories.add(new LastNameDomainFactory());
}

abstract VBindingDomain createDomain();

public static void addDefaultTextViewerExtensions(VBindingDomain domain) {
	domain.addExtensions(new TextViewerFocusBehaviourExtension());
}

public static VDataBindingFactory buildDataBindingFactory() {
	VBindingDomainRegistry domainReg = new VBindingDomainRegistry();
	
	for (SampleBindingDomainFactory f : factories)
		domainReg.addDomain(f.createDomain());
	
	return new VDataBindingFactory(domainReg);
}


}
