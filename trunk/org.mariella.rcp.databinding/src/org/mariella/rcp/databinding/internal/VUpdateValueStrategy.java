package org.mariella.rcp.databinding.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.internal.databinding.BindingMessages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.mariella.rcp.databinding.SWTObservableStatusDecorator;
import org.mariella.rcp.databinding.VDataBindingContext;


public class VUpdateValueStrategy extends UpdateValueStrategy {
	
private Log log = LogFactory.getLog(VUpdateValueStrategy.class);

public VDataBindingContext dbc;
public ISWTObservableValue swtObservable;
public String conversionError = null;

public VUpdateValueStrategy(VDataBindingContext dbc) {
	super();
	this.dbc = dbc;
}

protected IStatus doSet(IObservableValue observableValue, Object value) {
	try {
		observableValue.setValue(value);
	} catch (Exception ex) {
		log.error("", ex);
	}
	return Status.OK_STATUS;
}

public Object convert(Object value) {
	conversionError = null;
	try {
		return super.convert(value);
	} catch (Throwable t) {
		if (log.isDebugEnabled()) log.debug("Conversion error: " + t.getMessage());
		conversionError = t.getMessage();
		return null;
	}
}

public IStatus validateAfterConvert(Object value) {
	IStatus status;
	if (conversionError != null)
		status = ValidationStatus.error(conversionError);
	else
		status = super.validateAfterConvert(value);
	if (swtObservable != null) {
		SWTObservableStatusDecorator statusDecorator = dbc.swtObservableStatusDecoratorMap.get(swtObservable);
		if (statusDecorator != null)
			statusDecorator.decorateStatus(swtObservable, status);
	}
	return status;
}



}
