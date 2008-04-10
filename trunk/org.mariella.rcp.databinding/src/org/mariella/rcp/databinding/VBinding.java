package org.mariella.rcp.databinding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;

public class VBinding {

public interface DisposeListener {
void disposed(VBinding binding);
}

private VBindingContext bindingContext;
private VBindingDomain domain;
private Binding[] bindings;
private List<DisposeListener> disposeListeners = new ArrayList<DisposeListener>();

VBinding(VBindingContext bindingContext, Binding binding, VBindingDomain domain) {
	this(bindingContext, new Binding[]{binding}, domain);
}

VBinding(VBindingContext bindingContext, Binding[] bindings, VBindingDomain domain) {
	this.bindingContext = bindingContext;
	this.bindings = bindings;
	this.domain = domain;
}

public void addDisposeListener(DisposeListener l) {
	disposeListeners.add(l);
}

public Binding getBinding() {
	return bindings[0];
}

public Binding[] getBindings() {
	return bindings;
}

public VBindingDomain getDomain() {
	return domain;
}

public VBindingContext getBindingContext() {
	return bindingContext;
}

@Override
public String toString() {
	return "Binding for domain: " + domain.getSymbol();
}

public final void dispose() {
	// Binding.dispose() is called by DataBindingContext
	
	for (DisposeListener l : disposeListeners)
		l.disposed(this);
}

}
