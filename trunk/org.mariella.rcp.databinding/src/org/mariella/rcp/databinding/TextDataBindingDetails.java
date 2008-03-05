package org.mariella.rcp.databinding;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class TextDataBindingDetails {

public int eventType;
public SWTObservableStatusDecorator statusDecorator;

public TextDataBindingDetails() {
	this(SWT.Modify);
}
public TextDataBindingDetails(int eventType) {
	this(eventType, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}
public TextDataBindingDetails(int eventType, SWTObservableStatusDecorator statusDecorator) {
	this.eventType = eventType;
	this.statusDecorator = statusDecorator;
}

}
