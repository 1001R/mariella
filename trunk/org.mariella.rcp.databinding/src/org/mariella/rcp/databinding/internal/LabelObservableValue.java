package org.mariella.rcp.databinding.internal;


import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTVetoableValue;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class LabelObservableValue extends AbstractSWTVetoableValue implements VTargetObservable {

private final Label label;

public LabelObservableValue(final Label label) {
	super(label);
	this.label = label;
}

public boolean isResponsibleFor(Control control) {
	return control == label;
}


protected void doSetApprovedValue(final Object value) {
	label.setText(value == null ? "" : value.toString()); //$NON-NLS-1$
}

public Object doGetValue() {
	return label.getText();
}

/**
 * Returns the type of the value from {@link #doGetValue()}, i.e.
 * String.class
 * 
 * @see org.eclipse.core.databinding.observable.value.IObservableValue#getValueType()
 */
public Object getValueType() {
	return String.class;
}

public void extensionsInstalled() {
}

public boolean blockDefaultTraversing() {
	return false;
}

}
