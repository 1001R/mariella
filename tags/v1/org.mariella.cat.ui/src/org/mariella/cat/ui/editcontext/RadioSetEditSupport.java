package org.mariella.cat.ui.editcontext;

import org.mariella.cat.core.validation.Messages;


public abstract class RadioSetEditSupport<T> extends EditSupportAdapter<T> {

	public RadioSetEditSupport() {
	}
	
	@Override
	public void uiValueChanged(EditContext editContext, T uiValue, Object source, Messages messages) {
		setModelValue(uiValue);
	}
	
	protected abstract void setModelValue(T modelValue);

}
