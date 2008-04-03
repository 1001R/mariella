package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;

public abstract class ConverterBuilder {

public abstract IConverter buildToModelConverter(VBindingDomain domain);
public abstract IConverter buildFromModelConverter(VBindingDomain domain);
public IValidator buildBeforeSetModelValidator(VBindingDomain domain) {
	return null;
}

}
