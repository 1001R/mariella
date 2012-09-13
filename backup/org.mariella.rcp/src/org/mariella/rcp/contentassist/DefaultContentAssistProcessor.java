package org.mariella.rcp.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


public class DefaultContentAssistProcessor extends AbstractContentAssistProcessor {

List<String> availableValues;

public DefaultContentAssistProcessor(List<String> availableValues) {
	this.availableValues = availableValues;
}

public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
	IDocument doc = viewer.getDocument();
	String curWord = doc.get().substring(0, offset);
	
	List<String> suggestions = new ArrayList<String>();
	for (String name : availableValues) {
		if (name.toLowerCase().startsWith(curWord.toLowerCase()))
			suggestions.add(name);
	}
	
	ICompletionProposal[] proposals = null;
	if (suggestions.size() > 0) {
		proposals = buildProposals(suggestions, curWord, offset-curWord.length());
		lastError = null;
	}
	return proposals;
}

}
