package org.mariella.rcp.databinding.contentassist;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;


public class ContentAssistantController implements DisposeListener {

TextViewer textViewer;
VContentAssistProcessor contentAssistProcessor;
ContentAssistantExtension contentAssistantExtension;
ContentAssistant contentAssistant;
boolean contentAssistantOpen = false;
ICompletionProposal currentSelectedCompletionProposal;
boolean enabled = true;

private static Map<ITextViewer,ContentAssistantController> controllerMap = new HashMap<ITextViewer, ContentAssistantController>();

public ContentAssistantController(TextViewer textViewer, ContentAssistantExtension caExtension, Object domainContext) {
	this.textViewer = textViewer;
	this.contentAssistProcessor = caExtension.createContentAssistProcessor(domainContext);
	this.contentAssistantExtension = caExtension;
}

public void installContentAssistant() {
	contentAssistant = new ContentAssistant();
	controllerMap.put(textViewer, this);
	textViewer.getControl().addDisposeListener(this);
	contentAssistant.setContentAssistProcessor(contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
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
	contentAssistant.install(textViewer);
	textViewer.getTextWidget().addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (!contentAssistantOpen && (e.stateMask == SWT.CTRL && e.character == ' ' || Character.isLetterOrDigit(e.character))) {
				showCompletionsIfPossible();
				return;
			}
		}
	});
}

void showCompletionsIfPossible() {
	if (!enabled) return;
	textViewer.getTextWidget().getDisplay().asyncExec(new Runnable() {
		public void run() {
			if (!textViewer.getTextWidget().isFocusControl()) return;
			
			if (contentAssistProcessor.initializeProposals(textViewer, textViewer.getSelectedRange().x))
				contentAssistant.showPossibleCompletions();
		}
	});
	
}

public void setEnabled(boolean enabled) {
	if (!enabled)
		contentAssistant.uninstall();
	else
		contentAssistant.install(textViewer);
	this.enabled = enabled;
}

public boolean isContentAssistantOpen() {
	return contentAssistantOpen;
}

@Override
public void widgetDisposed(DisposeEvent e) {
	controllerMap.remove(textViewer);
}

public static boolean isContentAssistantOpen(TextViewer textViewer) {
	ContentAssistantController controller = controllerMap.get(textViewer);
	if (controller == null)
		return false;
	return controller.isContentAssistantOpen();
}

}
