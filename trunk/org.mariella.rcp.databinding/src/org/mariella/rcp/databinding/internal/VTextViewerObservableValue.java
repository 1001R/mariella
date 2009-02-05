package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTVetoableValue;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.mariella.rcp.databinding.TextBindingDetails;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingSelection;
import org.mariella.rcp.databinding.contentassist.ContentAssistantController;

public class VTextViewerObservableValue extends AbstractSWTVetoableValue implements SelectionAwareObservable, EnabledObservableValueFactory, 
	VTargetObservable, ClipboardSupportingObservable {

private static final int[] validUpdateEventTypes = new int[] { SWT.Modify, SWT.FocusOut, SWT.NONE };

final TextViewer textViewer;
private final StyledText text;
private TextBindingDetails textBindingDetails;
private boolean updating = false;
private String oldValue;
private final SelectionDispatchingObservableSupport selectionDispatchingSupport;
ContentAssistantController contentAssistantController;
VBindingContext bindingContext;

private Listener updateListener = new Listener() {
	public void handleEvent(Event event) {
		if (!updating) {
			String newValue = text.getText();

			if (!newValue.equals(oldValue)) {
				fireValueChange(Diffs.createValueDiff(oldValue, newValue));					
				oldValue = newValue;
			}
		}
	}
};

private TraverseListener traverseListener = new TraverseListener() {
	@Override
	public void keyTraversed(TraverseEvent e) {
		if (!updating && e.detail == textBindingDetails.applyOnTraverseEventDetail) {
			String newValue = text.getText();

			if (!newValue.equals(oldValue)) {
				fireValueChange(Diffs.createValueDiff(oldValue, newValue));					
				oldValue = newValue;
			}
		}
	}
};

private VerifyListener verifyListener = new VerifyListener() {
	public void verifyText(VerifyEvent e) {
		if (e.text.equals("\t"))
			e.doit = false;
	}
};

private SelectionListener selectionListener = new SelectionListener() {
	public void widgetSelected(SelectionEvent e) {
		fireSelectionChanged();
	}
	public void widgetDefaultSelected(SelectionEvent e) {}
};

private IDocumentListener documentListener = new IDocumentListener() {
	String currentText;
	public void documentAboutToBeChanged(DocumentEvent event) {
		if (textBindingDetails.applyOnEventType == SWT.Modify || 
				(textBindingDetails.textViewerObservableCallback != null && textBindingDetails.textViewerObservableCallback.updateModelOnDocumentModification())) {
			currentText = text.getText();
		}
	}
	public void documentChanged(DocumentEvent e) {
		if (!updating) {
			String newText = text.getText();
			
			if (textBindingDetails.applyOnEventType == SWT.Modify || 
					(textBindingDetails.textViewerObservableCallback != null && textBindingDetails.textViewerObservableCallback.updateModelOnDocumentModification())) {
				fireValueChange(Diffs.createValueDiff(currentText, newText));
				fireSelectionChanged();
			}
		}
	}
};

private FocusListener focusListener = new FocusListener() {
	public void focusLost(FocusEvent e) {
	}
	public void focusGained(FocusEvent e) {
		fireSelectionChanged();
	}
};

public VTextViewerObservableValue(VBindingContext bindingContext, TextViewer textViewer, TextBindingDetails textBindingDetails) {
	super(textViewer.getTextWidget());
	this.bindingContext = bindingContext;
	this.textViewer = textViewer;
	selectionDispatchingSupport = new SelectionDispatchingObservableSupport(this, new ControlSelectionDecorator(textViewer.getTextWidget())); 
	boolean eventValid = false;
	for (int i = 0; !eventValid && i < validUpdateEventTypes.length; i++) {
		eventValid = (textBindingDetails.applyOnEventType == validUpdateEventTypes[i]);
	}
	if (!eventValid) {
		throw new IllegalArgumentException(
				"UpdateEventType [" + textBindingDetails.applyOnEventType + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}
	this.text = textViewer.getTextWidget();
	this.textBindingDetails = textBindingDetails;
	
	if (textBindingDetails.applyOnEventType == SWT.FocusOut) {
		// only work with this listener on FocusOut Events
		text.addListener(textBindingDetails.applyOnEventType, updateListener);
	}
	if (textBindingDetails.applyOnEventType == SWT.NONE && textBindingDetails.applyOnTraverseEventDetail != SWT.NONE) {
		text.addTraverseListener(traverseListener);
	}
	
	oldValue = text.getText();
	
	textViewer.getTextWidget().addVerifyListener(verifyListener);
	
	textViewer.getDocument().addDocumentListener(documentListener);
	textViewer.getTextWidget().addFocusListener(focusListener);
	text.addSelectionListener(selectionListener);
}

public boolean isResponsibleFor(Control control) {
	return control == textViewer.getControl();
}


@Override
public Object getValueType() {
	return String.class;
}

@Override
protected void doSetApprovedValue(final Object value) {
	try {
		updating = true;
		text.setText(value == null ? "" : value.toString());
		oldValue = text.getText();
	} finally {
		updating = false;
	}
}


@Override
protected Object doGetValue() {
	return oldValue = text.getText();
}

@Override
public void dispose() {
	if (!text.isDisposed()) {
		if (textBindingDetails.applyOnEventType != SWT.None) {
			text.removeListener(textBindingDetails.applyOnEventType, updateListener);
		}
		text.removeSelectionListener(selectionListener);
		text.removeFocusListener(focusListener);
		text.removeVerifyListener(verifyListener);
		if (textViewer.getDocument() != null) {
			textViewer.getDocument().removeDocumentListener(documentListener);
		}
		text.removeTraverseListener(traverseListener);
	}
	super.dispose();
}

public TextViewer getTextViewer() {
	return textViewer;
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
	if (textViewer != null && textViewer.getTextWidget() != null && !textViewer.getTextWidget().isDisposed() && 
			textViewer.getTextWidget().isFocusControl()) {
		VBindingSelection selection  = selectionDispatchingSupport.implementGetSelection();
		return selection;
	}
	return null;
}

public EnabledObservableValue createEnabledObservableValue() {
	return new VTextViewerEnabledObservableValue(this);
}

 void setEnabled(boolean enabled) {
	 textViewer.getTextWidget().setEditable(enabled);
	 textViewer.getTextWidget().setEnabled(enabled);
	 if (contentAssistantController != null)
		 contentAssistantController.setEnabled(enabled);
}

public void setContentAssistantController(ContentAssistantController controller) {
	this.contentAssistantController = controller;
}

public void setSelectionTargetControl(Control control) {
	selectionDispatchingSupport.setSelectionTargetControl(control);
}

public void installClipboardHandlerExtension(TextViewerClipboardHandlerExtension textViewerClipboardHandlerExtension) {
}

public void extensionsInstalled() {
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	selectionDispatchingSupport.setContextSelectionCallback(getContextSelectionCallback);
}

void fireSelectionChanged() {
	VBindingSelection selection = getSelection();
	if (selection == null) return;
	
	VBindingSelectionProvider selectionProvider = (VBindingSelectionProvider)bindingContext.getSelectionProvider(); 
	selectionProvider.fireSelectionChanged(selection);
}

public boolean canCut() {
	return canCopy() && canDelete();
}

public boolean canCopy() {
	return text.getSelection().x != text.getSelection().y;
}

public boolean canPaste() {
	return text.isEnabled();
}

public boolean canDelete() {
	return text.isEnabled();
}

public void doCopy() {
	text.invokeAction(ST.COPY);
}

public void doPaste() {
	text.invokeAction(ST.PASTE);
}

public void doCut() {
	text.invokeAction(ST.CUT);
}

public void doDelete() {
	text.invokeAction(ST.DELETE_WORD_NEXT);
}

public boolean blockDefaultTraversing() {
	if (contentAssistantController == null) return false;
	return contentAssistantController.isContentAssistantOpen();
}

}