package org.mariella.cat.ui.editcontext;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public class EditableControl implements IEditable {

	private final EditContext editContext;
	private final IEditSupport<?> editSupport;
	private final Control control;
	private final Listener listener;
	private final int[] eventTypes;

	public EditableControl(EditContext editContext, IEditSupport<?> editSupport, Control control, Listener listener, int... eventTypes) {
		this.editContext = editContext;
		this.editSupport = editSupport;
		this.control = control;
		this.listener = listener;
		this.eventTypes = eventTypes;
		for (int eventType : eventTypes) {
			control.addListener(eventType, listener);
		}
	}

	@Override
	public void validate() {
		editSupport.validate(editContext, control, editContext.getMessages());
	}

	@Override
	public void dispose() {
		for (int eventType : eventTypes) {
			control.removeListener(eventType, listener);
		}
	}
}