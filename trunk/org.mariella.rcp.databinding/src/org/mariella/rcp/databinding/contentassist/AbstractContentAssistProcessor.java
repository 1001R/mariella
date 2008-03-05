package org.mariella.rcp.databinding.contentassist;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public abstract class AbstractContentAssistProcessor implements VContentAssistProcessor {
protected String lastError = null;
protected IContextInformationValidator contextInfoValidator = new ContextInformationValidator(this);


protected ICompletionProposal[] buildProposals(List<String> suggestions, String replacedWord, int offset) {
	ICompletionProposal[] proposals = new ICompletionProposal[suggestions.size()];
	int index = 0;
	for (String sugg : suggestions) {
		proposals[index++] = new CompletionProposal(
				sugg, 
				offset, 
				replacedWord.length(),
				sugg.length()
				);
	}
	return proposals;
}

public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
	lastError = "No Context Information available";
	return null;
}

public char[] getCompletionProposalAutoActivationCharacters() {
	return null;
}

public char[] getContextInformationAutoActivationCharacters() {
	return null;
}

public IContextInformationValidator getContextInformationValidator() {
	return contextInfoValidator;
}

public String getErrorMessage() {
	return lastError;
}

}
