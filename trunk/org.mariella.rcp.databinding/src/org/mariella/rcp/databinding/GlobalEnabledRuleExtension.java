package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.EnabledObservableValueFactory;

public class GlobalEnabledRuleExtension implements DataBindingContextExtension {

EnabledCallback globalEnabledCallback;

VBindingFactory.Callback dataBindingFactoryCallback = new VBindingFactory.Callback() {
	public VBindingDomain extendBindingDomain(VBinding binding, VBindingDomain domain) {
		if (!(binding.getBinding().getTarget() instanceof EnabledObservableValueFactory)) return domain;
		
		EnabledRuleExtension existingEnabledExt = domain.getExtension(EnabledRuleExtension.class);
		if (existingEnabledExt != null) {
			VBindingDomain copy = new VBindingDomain(domain);
			EnabledRuleExtension modifiedEnabledExt = new EnabledRuleExtension(
					new CompoundEnabledCallback(existingEnabledExt.enabledCallback, globalEnabledCallback));
			copy.replaceExtension(existingEnabledExt, modifiedEnabledExt);
			return copy;
		}
		return new VBindingDomain(domain, new EnabledRuleExtension(globalEnabledCallback));
	}
	public void bindingCreated(VBinding binding) {}
};

public GlobalEnabledRuleExtension(EnabledCallback globalEnabledCallback) {
	this.globalEnabledCallback = globalEnabledCallback;
}


public void install(VBindingContext bindingContext) {
	bindingContext.dataBindingFactory.addCallback(dataBindingFactoryCallback);
}

public void dispose() {
}

}
