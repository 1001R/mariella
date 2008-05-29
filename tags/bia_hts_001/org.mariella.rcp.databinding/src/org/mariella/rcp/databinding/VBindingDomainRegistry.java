package org.mariella.rcp.databinding;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VBindingDomainRegistry {
Log log = LogFactory.getLog(VBindingDomainRegistry.class);

private Map<Object,VBindingDomain> registry = new HashMap<Object,VBindingDomain>();

public void addDomain(VBindingDomain domain) {
	registry.put(domain.getSymbol(), domain);
}

public VBindingDomain getDomain(Object symbol) {
	VBindingDomain domain = registry.get(symbol);
	if (domain == null) {
		String msg = "Domain with symbol " + symbol + " not found";
		log.error(msg);
		throw new RuntimeException(msg);
	}
	return domain;
}

}
