package org.mariella.rcp.databinding.internal;

import java.util.logging.Level;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.mariella.rcp.databinding.SWTObservableStatusDecorator;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VDataBindingPlugin;


public class VUpdateValueStrategy extends UpdateValueStrategy {

public VBindingContext dbc;
public ISWTObservableValue swtObservable;
public String conversionError = null;
private VUpdateValueStrategyObserver observer = null;

public VUpdateValueStrategy(VBindingContext dbc) {
	super();
	this.dbc = dbc;
}

public VUpdateValueStrategy(VBindingContext dbc, VUpdateValueStrategyObserver observer) {
	super();
	this.dbc = dbc;
	this.observer = observer;
}

@Override
protected IStatus doSet(IObservableValue observableValue, Object value) {
	try {
		observableValue.setValue(value);
		if (observer != null)
			observer.setValueOccured(observableValue, value);
	} catch (Exception ex) {
		VDataBindingPlugin.logger.log(Level.SEVERE, "", ex);
	}
	return Status.OK_STATUS;
}

@Override
public Object convert(Object value) {
	conversionError = null;
	try {
		return super.convert(value);
	} catch (Throwable t) {
		if (VDataBindingPlugin.logger.isLoggable(Level.FINE)) VDataBindingPlugin.logger.log(Level.FINE, "Conversion error: ", t);
		conversionError = t.getMessage();
		return null;
	}
}

@Override
public IStatus validateAfterConvert(Object value) {
	IStatus status;
	if (conversionError != null)
		status = ValidationStatus.error(conversionError);
	else
		status = super.validateAfterConvert(value);
	if (swtObservable != null) {
		SWTObservableStatusDecorator statusDecorator = ((InternalBindingContext)dbc).getMainContext().swtObservableStatusDecoratorMap.get(swtObservable);
		if (statusDecorator != null)
			statusDecorator.decorateStatus(swtObservable, status);
	}
	return status;
}



}
