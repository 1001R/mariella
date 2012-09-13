package org.mariella.rcp.databinding;

public class DisabledRuleExtension extends EnabledRuleExtension {

public DisabledRuleExtension() {
	super(new AbstractEnabledCallback() {
		public boolean isEnabled() {
			return false;
		}
	});
}

}
