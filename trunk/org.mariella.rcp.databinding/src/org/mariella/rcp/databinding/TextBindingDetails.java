package org.mariella.rcp.databinding;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class TextBindingDetails {

public int applyOnEventType;	// SWT.Modify, SWT.FocusOut, SWT.NONE 
public int applyOnTraverseEventDetail;	// e.g. SWT.TRAVERSE_RETURN;	only valid when applyOnEventTsype is set to SWT.NONE 
public SWTObservableStatusDecorator statusDecorator;

public TextBindingDetails() {
	this(SWT.Modify);
}

public TextBindingDetails(int applyOnEventType) {
	this(applyOnEventType, SWT.NONE, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(int applyOnEventType, int applyOnTraverseEventDetail) {
	this(applyOnEventType, applyOnTraverseEventDetail, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(int applyOnEventType, int applyOnTraverseEventDetail, SWTObservableStatusDecorator statusDecorator) {
	this.applyOnEventType = applyOnEventType;
	this.applyOnTraverseEventDetail = applyOnTraverseEventDetail;
	this.statusDecorator = statusDecorator;
}

}
