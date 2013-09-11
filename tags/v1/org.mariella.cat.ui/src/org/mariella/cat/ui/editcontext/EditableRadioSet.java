package org.mariella.cat.ui.editcontext;

import org.mariella.cat.ui.controls.radioset.RadioSet;

public class EditableRadioSet<T> implements IEditable {

	private final EditContext editContext;
	private final IEditSupport<?> editSupport;
	private final RadioSet<T> radioSet;
	private final RadioSet.IRadioSetListener<T> listener;

	public EditableRadioSet(EditContext editContext, IEditSupport<?> editSupport, RadioSet<T> radioSet, RadioSet.IRadioSetListener<T> listener) {
		this.editContext = editContext;
		this.editSupport = editSupport;
		this.radioSet = radioSet;
		this.listener = listener;
		radioSet.addRadioSetListener(listener);
	}

	@Override
	public void validate() {
		editSupport.validate(this.editContext, radioSet, editContext.getMessages());
	}

	@Override
	public void dispose() {
		radioSet.removeRadioSetListener(listener);
	}
}