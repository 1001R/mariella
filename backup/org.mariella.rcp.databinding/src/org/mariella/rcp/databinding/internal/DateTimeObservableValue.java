package org.mariella.rcp.databinding.internal;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTVetoableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.mariella.rcp.databinding.VBindingSelection;

public class DateTimeObservableValue extends AbstractSWTVetoableValue implements SelectionAwareObservable, VTargetObservable {

private final SelectionDispatchingObservableSupport selectionDispatchingSupport;

DateTime dateTime;
Calendar oldValue;
private boolean updating = false;

public DateTimeObservableValue(DateTime dateTime) {
	super(dateTime);
	this.dateTime = dateTime;
	selectionDispatchingSupport = new SelectionDispatchingObservableSupport(this, new ControlSelectionDecorator(dateTime));
	
	oldValue = readCalendarValue();
	
	dateTime.addSelectionListener(new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			if (!updating) {
				Calendar newValue = readCalendarValue();

				if (!newValue.equals(oldValue)) {
					fireValueChange(Diffs.createValueDiff(oldValue, newValue));					
					oldValue = newValue;
				}
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {}
	});
}

public boolean isResponsibleFor(Control control) {
	return control == dateTime;
}

@Override
public Object getValueType() {
	return Calendar.class;
}

@Override
protected void doSetApprovedValue(Object value) {
	try {
		updating = true;
		Calendar cal = value == null ? new GregorianCalendar() : (Calendar)value;
		if ((dateTime.getStyle() & SWT.DATE) == SWT.DATE) {
			dateTime.setYear(cal.get(Calendar.YEAR));
			dateTime.setMonth(cal.get(Calendar.MONTH));
			dateTime.setDay(cal.get(Calendar.DAY_OF_MONTH));
		}
		if ((dateTime.getStyle() & SWT.TIME) == SWT.TIME) {
			dateTime.setHours(cal.get(Calendar.HOUR_OF_DAY));
			dateTime.setMinutes(cal.get(Calendar.MINUTE));
		}
		oldValue = cal;
	} finally {
		updating = false;
	}
}

@Override
protected Object doGetValue() {
	return readCalendarValue();
}

public VDataBindingSelectionDispatcher getSelectionDispatcher() {
	return selectionDispatchingSupport;
}

public void setOffsetSelection(boolean offsetSelection) {
	selectionDispatchingSupport.setOffsetSelection(offsetSelection);
}

public void setSelectionBasePath(Object[] path) {
	selectionDispatchingSupport.implementSetSelectionBasePath(path);
}

public VBindingSelection getSelection() {
	if (dateTime != null && !dateTime.isDisposed() && dateTime.isFocusControl())
		return selectionDispatchingSupport.implementGetSelection();
	return null;
}

private Calendar readCalendarValue() {
	GregorianCalendar cal = new GregorianCalendar();
	if ((dateTime.getStyle() & SWT.DATE) == SWT.DATE) {
		cal.set(Calendar.YEAR, dateTime.getYear());
		cal.set(Calendar.MONTH, dateTime.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
	}
	if ((dateTime.getStyle() & SWT.TIME) == SWT.TIME) {
		cal.set(Calendar.HOUR_OF_DAY, dateTime.getHours());
		cal.set(Calendar.MINUTE, dateTime.getMinutes());
	}
	return cal;
}

public DateTime getDateTime() {
	return dateTime;
}

public void setSelectionTargetControl(Control control) {
	selectionDispatchingSupport.setSelectionTargetControl(control);
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
