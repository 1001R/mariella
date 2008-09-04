package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;

public interface EnabledCallback {

boolean isEnabled();

void install(EnabledStateModelObservableValue value);

void uninstall(EnabledStateModelObservableValue value);

}
