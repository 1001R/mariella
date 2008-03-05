package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;

public abstract class ConverterBuilder {

public abstract IConverter buildToModelConverter(BindingDomain domain);
public abstract IConverter buildFromModelConverter(BindingDomain domain);
public IValidator buildBeforeSetModelValidator(BindingDomain domain) {
	return null;
}

}
