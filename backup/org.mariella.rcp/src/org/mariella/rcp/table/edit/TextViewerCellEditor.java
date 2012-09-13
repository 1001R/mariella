package org.mariella.rcp.table.edit;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.mariella.rcp.contentassist.XContentAssistProcessor;


public class TextViewerCellEditor extends TableViewerEditControllerCellEditor {

public interface Callback extends TableViewerEditControllerCellEditor.Callback{
	XContentAssistProcessor getContentAssistProcessor(Object element);
	Object textToValue(String text) throws Exception;
	
}

TextViewer textViewer;
ContentAssistant contentAssistant = null;
XContentAssistProcessor contentAssistProcessor;
boolean hasFocus = false;
boolean contentAssistantOpen = false;
ICompletionProposal currentSelectedCompletionProposal;
boolean editable = false;
String lastTextValue = null;
Object lastValue = null;

public TextViewerCellEditor(TableViewerEditController controller, Callback callback) {
	super(controller, callback);
    create(controller.tableViewer.getTable());
}

@Override
protected Control createEditorControl(Composite parent) {
	textViewer = new TextViewer(parent, SWT.NONE  | SWT.SINGLE );
	textViewer.getTextWidget().addVerifyListener(new VerifyListener() {
		public void verifyText(VerifyEvent evt) {
			if (evt.text.contains("\t"))
				evt.doit = false;
		}
	});
	textViewer.getTextWidget().addModifyListener(new ModifyListener(){
		public void modifyText(ModifyEvent arg0) {
			if (!hasFocus) return;
			setEditMode(true);
			showCompletionsIfPossible();
            controller.fireContentModified();
		}
	});
	textViewer.getTextWidget().addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			if (!contentAssistantOpen)
				TextViewerCellEditor.this.focusLost();
		}
		@Override
		public void focusGained(FocusEvent e) {
			TextViewerCellEditor.this.focusGained();
		}
	});
	textViewer.getTextWidget().addTraverseListener(new TraverseListener() {
		public void keyTraversed(TraverseEvent ev) {
			if (ev.detail == SWT.TRAVERSE_TAB_NEXT ) {
				if (contentAssistantOpen && currentSelectedCompletionProposal != null) {
					currentSelectedCompletionProposal.apply(textViewer.getDocument());
				}
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						controller.processTabNextEvent(TextViewerCellEditor.this);
					}
				});
			} else if (ev.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
				if (contentAssistantOpen && currentSelectedCompletionProposal != null) {
					currentSelectedCompletionProposal.apply(textViewer.getDocument());
				}
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						controller.processTabPreviousEvent(TextViewerCellEditor.this);
					}
				});
			} else if (ev.detail == SWT.TRAVERSE_RETURN ) {
			}
		}
	});
	textViewer.getTextWidget().addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.stateMask == SWT.CTRL && e.character == ' ') {
				if (!isEditMode())
					setEditMode(true);
				showCompletionsIfPossible();
				return;
			}
			
			if (e.keyCode == SWT.ARROW_UP && !contentAssistantOpen) {
				controller.processCursorUpEvent(TextViewerCellEditor.this);
			} else if (e.keyCode == SWT.ARROW_DOWN  && !contentAssistantOpen) {
				controller.processCursorDownEvent(TextViewerCellEditor.this);
			} else if (!isEditMode() && e.keyCode == SWT.ARROW_LEFT  && !contentAssistantOpen && textViewer.getSelectedRange().x == 0) {
				controller.processTabPreviousEvent(TextViewerCellEditor.this);
			} else if (!isEditMode() && e.keyCode == SWT.ARROW_RIGHT && !contentAssistantOpen && textViewer.getSelectedRange().x == textViewer.getDocument().getLength()) {
				controller.processTabNextEvent(TextViewerCellEditor.this);
			}
		}
	});

	return textViewer.getTextWidget();
}


@Override
protected Object doGetValue() {
	Document doc = (Document)textViewer.getDocument();
	try {
		return ((Callback)callback).textToValue(doc.get());
	} catch (Exception e) {
        setErrorMessage(e.getMessage());
        return lastValue;
	}
}

@Override
protected void doSetFocus() {
	textViewer.getTextWidget().setFocus();
}

@Override
protected void doSetEditorValue(Object value) {
	lastValue = value;
	String text = callback.valueToText(value);
	lastTextValue = text;
	textViewer.setDocument(new Document(text));
	contentAssistProcessor = ((Callback)callback).getContentAssistProcessor(controller.getSelectedElement());
	if (contentAssistProcessor != null) {
		contentAssistant = new ContentAssistant();
		contentAssistant.addCompletionListener(new ICompletionListener() {
			public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
				currentSelectedCompletionProposal = proposal;
			}
			public void assistSessionStarted(ContentAssistEvent event) {
				contentAssistantOpen = true;
			}
			public void assistSessionEnded(ContentAssistEvent event) {
				contentAssistantOpen = false;
			}
		});
		contentAssistant.setContentAssistProcessor(contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		contentAssistant.install(textViewer);
	}
	textViewer.getTextWidget().selectAll();
}

protected void focusGained() {
	currentSelectedCompletionProposal = null;
	textViewer.getTextWidget().selectAll();
	hasFocus = true;
}

@Override
protected void focusLost() {
	if (textViewer == null || textViewer.getTextWidget() == null || textViewer.getTextWidget().isDisposed()) return;
	hasFocus = false;
	
	super.focusLost();
	if (!textViewer.getDocument().get().equals(lastTextValue)) {
		controller.fireContentModified();
	}
	if (contentAssistant != null)
		contentAssistant.uninstall();
}

void showCompletionsIfPossible() {
	if (contentAssistant == null) return;
	Display.getCurrent().asyncExec(new Runnable() {
		public void run() {
			if (contentAssistProcessor.hasProposals(textViewer, textViewer.getSelectedRange().x))
				contentAssistant.showPossibleCompletions();
		}
	});
	
}

}
