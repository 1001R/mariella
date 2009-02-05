package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.value.IObservableValue;

public interface VUpdateValueStrategyObserver {

void setValueOccured(IObservableValue target, Object value);
	
}
