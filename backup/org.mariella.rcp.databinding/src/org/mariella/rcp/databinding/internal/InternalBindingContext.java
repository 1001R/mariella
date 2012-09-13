package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;

public interface InternalBindingContext extends VBindingContext {

MainBindingContext getMainContext();

VBinding bindValue(IObservableValue targetObservableValue,
		IObservableValue modelObservableValue,
		UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget,
		VBindingDomain domain);

VBinding createBinding(Binding[] baseBindings, VBindingDomain domain);

}
