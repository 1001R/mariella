package org.mariella.cat.ui.editcontext;

import org.mariella.cat.core.validation.Messages;


public interface IEditSupport<U> {

	void uiValueChanged(EditContext editContext, U uiValue, Object source, Messages messages);

	void validate(EditContext editContext, Object source, Messages messages);

	void startEditing(EditContext editContext, Object source, Messages messages);

	void endEditing(EditContext editContext, Object source, Messages messages);

	boolean cancelEditing(EditContext editContext, Object source, Messages messages);

}
