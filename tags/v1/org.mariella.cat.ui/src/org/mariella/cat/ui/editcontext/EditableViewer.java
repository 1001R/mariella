package org.mariella.cat.ui.editcontext;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Listener;

public class EditableViewer implements IEditable {

	private final EditContext editContext;
	private final IEditSupport<?> editSupport;
	private final Viewer viewer;
	private final ISelectionChangedListener selectionChangedListener;
	private final Listener listener;
	private final int[] eventTypes;

	public EditableViewer(EditContext editContext, IEditSupport<?> editSupport, Viewer viewer, ISelectionChangedListener selectionChangedListener, Listener listener, int... eventTypes) {
		this.editContext = editContext;
		this.editSupport = editSupport;
		this.viewer = viewer;
		this.selectionChangedListener = selectionChangedListener;
		this.listener = listener;
		this.eventTypes = eventTypes;
		viewer.addSelectionChangedListener(selectionChangedListener);
		for (int eventType : eventTypes) {
			viewer.getControl().addListener(eventType, listener);
		}
	}

	@Override
	public void validate() {
		editSupport.validate(editContext, viewer.getControl(), editContext.getMessages());
	}

	public void dispose() {
		viewer.removeSelectionChangedListener(selectionChangedListener);
		for (int eventType : eventTypes) {
			viewer.getControl().removeListener(eventType, listener);
		}
	}
}