package org.mariella.rcp.databinding;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class VBindingDomainRegistry {

private Map<Object,VBindingDomain> registry = new HashMap<Object,VBindingDomain>();

public void addDomain(VBindingDomain domain) {
	registry.put(domain.getSymbol(), domain);
}

public VBindingDomain getDomain(Object symbol) {
	VBindingDomain domain = registry.get(symbol);
	if (domain == null) {
		String msg = "Domain with symbol " + symbol + " not found";
		VDataBindingPlugin.logger.log(Level.SEVERE, msg);
		throw new RuntimeException(msg);
	}
	return domain;
}

}
