package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;


public class CompoundEnabledCallback implements EnabledCallback {

EnabledCallback[] enabledCallbacks;

public CompoundEnabledCallback(EnabledCallback ... enabledCallbacks) {
	this.enabledCallbacks = enabledCallbacks;
}

public boolean isEnabled() {
	for (EnabledCallback cb : enabledCallbacks)
		if (!cb.isEnabled()) return false;
	return true;
}

public void install(EnabledStateModelObservableValue value) {
	for (EnabledCallback cb : enabledCallbacks)
		cb.install(value);
}

public void uninstall(EnabledStateModelObservableValue value) {
	for (EnabledCallback cb : enabledCallbacks)
		cb.uninstall(value);
}

}
