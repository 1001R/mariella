package org.mariella.rcp.databinding.internal;


import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTVetoableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.mariella.rcp.databinding.VBindingSelection;

public class VTextObservableValue extends AbstractSWTVetoableValue implements SelectionAwareObservable, EnabledObservableValueFactory, VTargetObservable {

private final Text text;
private boolean updating = false;
private final int updateEventType;
private static final int[] validUpdateEventTypes = new int[] { SWT.Modify,
		SWT.FocusOut, SWT.NONE };
private String oldValue;
private final SelectionDispatchingObservableSupport selectionDispatchingSupport;

private Listener updateListener = new Listener() {
	public void handleEvent(Event event) {
		if (!updating) {
			String newValue = text.getText();

			if (!newValue.equals(oldValue)) {
				fireValueChange(Diffs.createValueDiff(oldValue, newValue));					
				oldValue = newValue;
			}
		}
	}
};

private VerifyListener verifyListener;
public VTextObservableValue(final Text text, int updateEventType, int traverseEventType) {
	super(text);
	if (traverseEventType != SWT.NONE)
		throw new RuntimeException("Traverse events are not yet supported for Text controls");
	boolean eventValid = false;
	for (int i = 0; !eventValid && i < validUpdateEventTypes.length; i++) {
		eventValid = (updateEventType == validUpdateEventTypes[i]);
	}
	if (!eventValid) {
		throw new IllegalArgumentException(
				"UpdateEventType [" + updateEventType + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}
	this.text = text;
	this.updateEventType = updateEventType;
	if (updateEventType != SWT.None) {
		text.addListener(updateEventType, updateListener);
	}
	
	oldValue = text.getText();
	
	verifyListener = new VerifyListener() {
		public void verifyText(VerifyEvent e) {
			if (!updating) {
				String currentText = VTextObservableValue.this.text
						.getText();
				String newText = currentText.substring(0, e.start) + e.text
						+ currentText.substring(e.end);
				if (!fireValueChanging(Diffs.createValueDiff(currentText,
						newText))) {
					e.doit = false;
				}
			}
		}
	};
	text.addVerifyListener(verifyListener);
	
	selectionDispatchingSupport = new SelectionDispatchingObservableSupport(this, new ControlSelectionDecorator(text)); 

}

public boolean isResponsibleFor(Control control) {
	return control == text;
}


/**
 * Sets the bound {@link Text Text's} text to the passed <code>value</code>.
 * 
 * @param value
 *            new value, String expected
 * @see org.eclipse.core.databinding.observable.value.AbstractVetoableValue#doSetApprovedValue(java.lang.Object)
 * @throws ClassCastException
 *             if the value is anything other than a String
 */
@Override
protected void doSetApprovedValue(final Object value) {
	try {
		updating = true;
		text.setText(value == null ? "" : value.toString()); //$NON-NLS-1$
		oldValue = text.getText();
	} finally {
		updating = false;
	}
}

/**
 * Returns the current value of the {@link Text}.
 * 
 * @see org.eclipse.core.databinding.observable.value.AbstractVetoableValue#doGetValue()
 */
@Override
public Object doGetValue() {
	return oldValue = text.getText();
}

/**
 * Returns the type of the value from {@link #doGetValue()}, i.e.
 * String.class
 * 
 * @see org.eclipse.core.databinding.observable.value.IObservableValue#getValueType()
 */
@Override
public Object getValueType() {
	return String.class;
}

@Override
public void dispose() {
	if (!text.isDisposed()) {
		if (updateEventType != SWT.None) {
			text.removeListener(updateEventType, updateListener);
		}
		text.removeVerifyListener(verifyListener);
	}
	super.dispose();
}

public VDataBindingSelectionDispatcher getSelectionDispatcher() {
	return selectionDispatchingSupport;
}

public void setOffsetSelection(boolean offsetSelection) {
	selectionDispatchingSupport.setOffsetSelection(offsetSelection);
}

public VBindingSelection getSelection() {
	if (text != null && !text.isDisposed() && text.isFocusControl())
		return selectionDispatchingSupport.implementGetSelection();
	return null;
}

public void setSelectionBasePath(Object[] path) {
	selectionDispatchingSupport.implementSetSelectionBasePath(path);
}

public void setSelectionTargetControl(Control control) {
	selectionDispatchingSupport.setSelectionTargetControl(control);
}
public EnabledObservableValue createEnabledObservableValue() {
	return new DefaultEnabledObservableValue(text);
}

public void extensionsInstalled() {
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	selectionDispatchingSupport.setContextSelectionCallback(getContextSelectionCallback);
}

public boolean blockDefaultTraversing() {
	return false;
}

}
