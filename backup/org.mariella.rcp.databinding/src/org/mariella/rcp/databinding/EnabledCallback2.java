package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;

public interface EnabledCallback2 extends EnabledCallback {

	void install(EnabledStateModelObservableValue value);

	void uninstall(EnabledStateModelObservableValue value);

}
