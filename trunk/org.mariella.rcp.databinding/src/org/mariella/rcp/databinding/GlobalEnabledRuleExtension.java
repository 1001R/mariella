package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.EnabledObservableValueFactory;

public class GlobalEnabledRuleExtension implements DataBindingContextExtension {

EnabledCallback globalEnabledCallback;

VDataBindingFactory.Callback dataBindingFactoryCallback = new VDataBindingFactory.Callback() {
	public BindingDomain extendBindingDomain(VBinding binding, BindingDomain domain) {
		if (!(binding.getBinding().getTarget() instanceof EnabledObservableValueFactory)) return domain;
		
		EnabledRuleExtension existingEnabledExt = domain.getExtension(EnabledRuleExtension.class);
		if (existingEnabledExt != null) {
			BindingDomain copy = new BindingDomain(domain);
			EnabledRuleExtension modifiedEnabledExt = new EnabledRuleExtension(
					new CompoundEnabledCallback(existingEnabledExt.enabledCallback, globalEnabledCallback));
			copy.replaceExtension(existingEnabledExt, modifiedEnabledExt);
			return copy;
		}
		return new BindingDomain(domain, new EnabledRuleExtension(globalEnabledCallback));
	}
	public void bindingCreated(VBinding binding) {}
};

public GlobalEnabledRuleExtension(EnabledCallback globalEnabledCallback) {
	this.globalEnabledCallback = globalEnabledCallback;
}


public void install(VDataBindingContext dataBindingContext) {
	dataBindingContext.dataBindingFactory.addCallback(dataBindingFactoryCallback);
}

public void dispose() {
}

}
