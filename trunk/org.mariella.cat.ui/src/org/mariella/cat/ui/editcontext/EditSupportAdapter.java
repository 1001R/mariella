package org.mariella.cat.ui.editcontext;

import org.mariella.cat.core.validation.Messages;

public abstract class EditSupportAdapter<U> implements IEditSupport<U> {

	@Override
	public void uiValueChanged(EditContext editContext, U uiValue, Object source, Messages messages) {
	}

	@Override
	public void validate(EditContext editContext, Object source, Messages messages) {
	}

	@Override
	public void startEditing(EditContext editContext, Object source, Messages messages) {
	}

	@Override
	public void endEditing(EditContext editContext, Object source, Messages messages) {
	}

	@Override
	public boolean cancelEditing(EditContext editContext, Object source, Messages messages) {
		return false;
	}

}
