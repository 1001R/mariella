package org.mariella.rcp.databinding;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class TextBindingDetails {

public int eventType;
public SWTObservableStatusDecorator statusDecorator;

public TextBindingDetails() {
	this(SWT.Modify);
}
public TextBindingDetails(int eventType) {
	this(eventType, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}
public TextBindingDetails(int eventType, SWTObservableStatusDecorator statusDecorator) {
	this.eventType = eventType;
	this.statusDecorator = statusDecorator;
}

}
