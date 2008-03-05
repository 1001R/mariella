package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.observable.IObservable;
import org.mariella.rcp.databinding.internal.EnabledObservableValueFactory;

public class EnabledRuleExtension implements BindingDomainExtension {


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

public void install(VBinding binding) {
	IObservable observable = binding.getBinding().getTarget();
	if (!(observable instanceof EnabledObservableValueFactory))
		throw new IllegalStateException();
	if (bean == null || dependencyPropertyPathes == null) {
		binding.getDataBindingContext().dataBindingFactory.createEnabledBinding(
				binding.getDataBindingContext(),
				(EnabledObservableValueFactory)observable,
				enabledCallback
				);
	} else {
		binding.getDataBindingContext().dataBindingFactory.createEnabledBinding(
				binding.getDataBindingContext(),
				(EnabledObservableValueFactory)observable,
				bean,
				enabledCallback,
				dependencyPropertyPathes
				);
	}
}

}
