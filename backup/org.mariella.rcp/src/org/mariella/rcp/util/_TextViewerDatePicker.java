package org.mariella.rcp.util;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.tiff.common.ui.datepicker.DatePicker;

public class _TextViewerDatePicker extends Composite {

public static interface Callback {
	TextViewer createDateTextViewer(Composite parent);
	DateFormat getDateFormat();
}

Callback callback;
TextViewer textViewer;
Button dropDownButton; 
DateFormat dateFormat;
SelectionListener buttonListener = new SelectionListener() {
	public void widgetDefaultSelected(SelectionEvent e) {}
	public void widgetSelected(SelectionEvent e) {
		openPopup();	
	}
};
Shell currentPopup;

public _TextViewerDatePicker(Composite parent, int style, Callback callback) {
	super(parent, style);
	this.callback = callback;
	dateFormat = callback.getDateFormat();
	initialize();
}

private void initialize() {
	setLayout(new FormLayout());
	textViewer = callback.createDateTextViewer(this);
	dropDownButton = new Button(this, SWT.ARROW | SWT.DOWN | SWT.NO_FOCUS);
	
	FormData formData = new FormData();
	formData.top = new FormAttachment(0);
	formData.left = new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(dropDownButton);
	textViewer.getTextWidget().setLayoutData(formData);
	
	formData = new FormData();
	formData.top = new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(100);
	dropDownButton.setLayoutData(formData);
	
	dropDownButton.addSelectionListener(buttonListener);
}

@Override
public void dispose() {
	dropDownButton.removeSelectionListener(buttonListener);
	super.dispose();
}

void openPopup() {
	if (currentPopup != null) {
		closePopup(currentPopup);
		return;
	}
	if (!textViewer.getTextWidget().isEnabled()) return;
	
	final Shell popup = new Shell(getShell(), SWT.NONE);
	popup.setLayout(new FillLayout());
	Composite comp = new Composite(popup, SWT.NONE);
	comp.setLayout(new FillLayout());
	final DatePicker datePicker = new DatePicker(comp, SWT.SINGLE | SWT.FLAT);
	popup.pack();

	Rectangle textBounds = getBounds();
	Point textPos = getParent().toDisplay(getLocation());
	Point popupLocation = new Point(textPos.x, textPos.y+textBounds.height); 
	popup.setLocation(popupLocation);
	datePicker.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			popup.setVisible(false);
			popup.dispose();
		}
	});
	Date date = getDateFromText();
	if (date != null) {
		datePicker.setDate(date);
	}
	datePicker.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			textViewer.getDocument().set(dateFormat.format(datePicker.getDate()));
			closePopup(popup);
		}
	});
	textViewer.getTextWidget().setFocus();
	textViewer.getTextWidget().addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			System.out.println("focus lost");
			closePopup(popup);
		}
	});
	popup.addDisposeListener(new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			currentPopup = null;
		}
	});
	currentPopup = popup;
	popup.setVisible(true);
}

void closePopup(final Shell popup) {
	if (popup != null && !popup.isDisposed()) 	popup.setVisible(false);
	Display.getCurrent().asyncExec(new Runnable() {
		public void run() {
			if (popup != null && !popup.isDisposed()) 	popup.dispose();
		}
	});
}

private Date getDateFromText() {
	try {
		return dateFormat.parse(textViewer.getDocument().get()); 
	} catch (Exception e) {
		return null;
	}
}

}
