package org.mariella.rcp.databinding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;

public class VBinding {

public interface DisposeListener {
void disposed(VBinding binding);
}

private VDataBindingContext dataBindingContext;
private BindingDomain domain;
private Binding binding;
private List<DisposeListener> disposeListeners = new ArrayList<DisposeListener>();

VBinding(VDataBindingContext dataBindingContext, Binding binding, BindingDomain domain) {
	this.dataBindingContext = dataBindingContext;
	this.binding = binding;
	this.domain = domain;
}

public void addDisposeListener(DisposeListener l) {
	disposeListeners.add(l);
}

public Binding getBinding() {
	return binding;
}

public BindingDomain getDomain() {
	return domain;
}

public VDataBindingContext getDataBindingContext() {
	return dataBindingContext;
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
