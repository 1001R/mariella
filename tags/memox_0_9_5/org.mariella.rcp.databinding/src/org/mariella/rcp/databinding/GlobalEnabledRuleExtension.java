package org.mariella.rcp.databinding;


public class GlobalEnabledRuleExtension implements DataBindingContextExtension {

EnabledCallback globalEnabledCallback;

VBindingFactory.Callback dataBindingFactoryCallback = new VBindingFactory.Callback() {
	/**
	 * Implementation installs or extens existing instances of EnabledRuleExtension with the 
	 * given globalEnabledCallback.  
	 * 
	 */
	public VBindingDomain extendBindingDomain(VBinding binding, VBindingDomain domain) {
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
	bindingContext.getBindingFactory().addCallback(dataBindingFactoryCallback);
}

public void dispose() {
}

}
