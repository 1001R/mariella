package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;


public class CompoundEnabledCallback implements EnabledCallback2 {

EnabledCallback[] enabledCallbacks;

public CompoundEnabledCallback(EnabledCallback enabledCallback1, EnabledCallback enabledCallback2) {
	this.enabledCallbacks = new EnabledCallback[2];
	this.enabledCallbacks[0] = enabledCallback1;
	this.enabledCallbacks[1] = enabledCallback2;
}

public CompoundEnabledCallback(EnabledCallback ... enabledCallbacks) {
	this.enabledCallbacks = enabledCallbacks;
}

public CompoundEnabledCallback(EnabledCallback enabledCallack, EnabledCallback ... enabledCallbacks) {
	this.enabledCallbacks = new EnabledCallback[1 + enabledCallbacks.length];
	this.enabledCallbacks[0]= enabledCallack;
	System.arraycopy(enabledCallbacks, 0, this.enabledCallbacks, 1, enabledCallbacks.length);
}

public boolean isEnabled() {
	for (EnabledCallback cb : enabledCallbacks)
		if (!cb.isEnabled()) return false;
	return true;
}

public void install(EnabledStateModelObservableValue value) {
	for (EnabledCallback cb : enabledCallbacks) {
		if (cb instanceof EnabledCallback2)
			((EnabledCallback2)cb).install(value);
	}
}

public void uninstall(EnabledStateModelObservableValue value) {
	for (EnabledCallback cb : enabledCallbacks) {
		if (cb instanceof EnabledCallback2)
			((EnabledCallback2)cb).uninstall(value);
	}
}

}
