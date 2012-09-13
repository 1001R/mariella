package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Control;

public interface SelectionDecorator {

void decorateSelected();

void setSelectionTargetControl(Control control);

}
