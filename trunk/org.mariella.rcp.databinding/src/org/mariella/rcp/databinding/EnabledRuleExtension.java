package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.IObservable;
import org.mariella.rcp.databinding.internal.EnabledObservableValueFactory;

public class EnabledRuleExtension implements VBindingDomainExtension {


private Object bean;
private String[] dependencyPropertyPathes;
EnabledCallback enabledCallback;

public EnabledRuleExtension(Object bean, EnabledCallback enabledCallback, String ... dependencyPropertyPathes) {
	this.bean = bean;
	this.dependencyPropertyPathes = dependencyPropertyPathes;
	this.enabledCallback = enabledCallback;
}

public EnabledRuleExtension(EnabledCallback enabledCallback) {
	this.bean = null;
	this.dependencyPropertyPathes = null;
	this.enabledCallback = enabledCallback;
}

public EnabledRuleExtension copyExtend(EnabledCallback ...addCallbacks) {
	EnabledCallback[] newCallbacks = new EnabledCallback[addCallbacks.length+1];
	newCallbacks[0] = enabledCallback;
	System.arraycopy(addCallbacks, 0, newCallbacks, 1, addCallbacks.length);
	
	EnabledRuleExtension copy = new EnabledRuleExtension(bean, 
			new CompoundEnabledCallback(newCallbacks),
			this.dependencyPropertyPathes);
	
	return copy;
}

public void install(VBinding binding) {
	for (Binding b : binding.getBindings()) {
		IObservable observable = b.getTarget();
		if (!(observable instanceof EnabledObservableValueFactory))
			throw new IllegalStateException();
		if (bean == null || dependencyPropertyPathes == null) {
			binding.getBindingContext().bindingFactory.createEnabledBinding(
					binding.getBindingContext(),
					(EnabledObservableValueFactory)observable,
					enabledCallback
					);
		} else {
			binding.getBindingContext().bindingFactory.createEnabledBinding(
					binding.getBindingContext(),
					(EnabledObservableValueFactory)observable,
					bean,
					enabledCallback,
					dependencyPropertyPathes
					);
		}
	}
}

}
