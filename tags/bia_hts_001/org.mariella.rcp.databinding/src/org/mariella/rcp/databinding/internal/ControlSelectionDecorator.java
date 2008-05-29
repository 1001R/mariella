package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Control;

public class ControlSelectionDecorator implements SelectionDecorator {

Control control;

public ControlSelectionDecorator(Control control) {
	this.control = control;
}

public void decorateSelected() {
	control.setFocus();
}

public void setSelectionTargetControl(Control control) {
	this.control = control;
}

}
