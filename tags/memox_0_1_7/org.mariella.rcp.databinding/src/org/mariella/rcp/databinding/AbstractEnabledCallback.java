package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;

public abstract class AbstractEnabledCallback implements EnabledCallback {

public void install(EnabledStateModelObservableValue value) {}

public void uninstall(EnabledStateModelObservableValue value) {}

}
