package org.mariella.rcp.databinding;

import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class TextBindingDetails {

public int applyOnEventType;	// SWT.Modify, SWT.FocusOut, SWT.NONE 
public int applyOnTraverseEventDetail;	// e.g. SWT.TRAVERSE_RETURN;	only valid when applyOnEventTsype is set to SWT.NONE
public RefreshAfterTextInputCallback refreshAfterInputCallback = null;	// after an user input has occured, refresh the contents of the text field.
public SWTObservableStatusDecorator statusDecorator;
public TextViewerObservableCallback textViewerObservableCallback = null;

public TextBindingDetails() {
	this(SWT.Modify);
}

public TextBindingDetails(final int applyOnEventType) {
	this(new TextViewerObservableCallback() {
		@Override
		public boolean updateModelOnDocumentModification() {
			return applyOnEventType == SWT.Modify;
		}
	}, SWT.NONE, null, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(final int applyOnEventType, int applyOnTraverseEventDetail, RefreshAfterTextInputCallback refreshAfterInputCallback) {
	this(new TextViewerObservableCallback() {
		@Override
		public boolean updateModelOnDocumentModification() {
			return applyOnEventType == SWT.Modify;
		}
	}, applyOnTraverseEventDetail, refreshAfterInputCallback, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(final int applyOnEventType, int applyOnTraverseEventDetail, final boolean refreshAfterInput) {
	this(new TextViewerObservableCallback() {
		@Override
		public boolean updateModelOnDocumentModification() {
			return applyOnEventType == SWT.Modify;
		}
	}, applyOnTraverseEventDetail, new RefreshAfterTextInputCallback() {
		@Override
		public boolean refreshAfterTextInput() {
			return refreshAfterInput;
		}
	}, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(TextViewerObservableCallback cb) {
	this(cb, SWT.NONE, null, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(TextViewerObservableCallback cb, int applyOnTraverseEventDetail, RefreshAfterTextInputCallback refreshAfterInputCallback) {
	this(cb, applyOnTraverseEventDetail, refreshAfterInputCallback, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(TextViewerObservableCallback cb, int applyOnTraverseEventDetail, final boolean refreshAfterInput) {
	this(cb, applyOnTraverseEventDetail, new RefreshAfterTextInputCallback() {
		@Override
		public boolean refreshAfterTextInput() {
			return refreshAfterInput;
		}
	}, new ForegroundStatusDecorator(Display.getCurrent().getSystemColor(SWT.COLOR_RED)));
}

public TextBindingDetails(TextViewerObservableCallback cb, int applyOnTraverseEventDetail, RefreshAfterTextInputCallback refreshAfterInputCallback, SWTObservableStatusDecorator statusDecorator) {
	this.textViewerObservableCallback = cb;
	this.applyOnTraverseEventDetail = applyOnTraverseEventDetail;
	this.statusDecorator = statusDecorator;
	this.refreshAfterInputCallback = refreshAfterInputCallback;
}

}
