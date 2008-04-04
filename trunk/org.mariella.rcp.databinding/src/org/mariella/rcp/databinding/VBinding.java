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
private Binding binding;
private List<DisposeListener> disposeListeners = new ArrayList<DisposeListener>();

VBinding(VBindingContext bindingContext, Binding binding, VBindingDomain domain) {
	this.bindingContext = bindingContext;
	this.binding = binding;
	this.domain = domain;
}

public void addDisposeListener(DisposeListener l) {
	disposeListeners.add(l);
}

public Binding getBinding() {
	return binding;
}

public VBindingDomain getDomain() {
	return domain;
}

public VBindingContext getBindingContext() {
	return bindingContext;
}

public String toString() {
	return "Binding for domain: " + domain.getSymbol();
}

public final void dispose() {
	// Binding.dispose() is called by DataBindingContext
	
	for (DisposeListener l : disposeListeners)
		l.disposed(this);
}

}
