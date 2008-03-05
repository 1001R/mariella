package org.mariella.rcp.databinding;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BindingDomainRegistry {
Log log = LogFactory.getLog(BindingDomainRegistry.class);

private Map<Object,BindingDomain> registry = new HashMap<Object,BindingDomain>();

public void addDomain(BindingDomain domain) {
	registry.put(domain.getSymbol(), domain);
}

public BindingDomain getDomain(Object symbol) {
	BindingDomain domain = registry.get(symbol);
	if (domain == null) {
		String msg = "Domain with symbol " + symbol + " not found";
		log.error(msg);
		throw new RuntimeException(msg);
	}
	return domain;
}

}
