package org.mariella.rcp.adapters;

import org.mariella.rcp.databinding.VBindingContext;

public abstract class AbstractAdapterContext implements AdapterContext {

	public final VBindingContext bindingContext;
	
public AbstractAdapterContext(VBindingContext bindingContext) {
	this.bindingContext = bindingContext;
}

public VBindingContext getBindingContext() {
	return bindingContext;
}
	
}
