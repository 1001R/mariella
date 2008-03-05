package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;


public class ForegroundStatusDecorator extends SWTObservableStatusDecorator {

Color originColor;
Color errorColor;
String originTooltip;

public ForegroundStatusDecorator(Color errorColor) {
	this.errorColor = errorColor;
}

protected void initializeFor(Control control) {
	originColor = control.getForeground();
	originTooltip = control.getToolTipText();
}

public void decorateStatus(ISWTObservable observable, IStatus validationStatus) {
	
	if (validationStatus.getSeverity() == ValidationStatus.ERROR) {
		((Control)observable.getWidget()).setForeground(errorColor);
		((Control)observable.getWidget()).setToolTipText(validationStatus.getMessage());
	} else {
		((Control)observable.getWidget()).setForeground(originColor);
		((Control)observable.getWidget()).setToolTipText(originTooltip);
	}
}

}