package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class VCompoundValidator implements IValidator {

IValidator[] validators;

public VCompoundValidator(IValidator[] validators) {
	this.validators = validators;
}

public IStatus validate(Object value) {
	for (IValidator validator : validators) {
		IStatus status = validator.validate(value);
		if (!status.isOK())
			return status;
	}
	return Status.OK_STATUS;
}

}
