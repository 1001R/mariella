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
private ICompletionProposal[] currentProposals;

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

/**
 * Called before the content assistant opens to see if proposals are available.
 * returns true if proposals are available
 * 
 */
public final boolean initializeProposals(ITextViewer viewer, int offset) {
	currentProposals = buildProposals(viewer, offset);
	return currentProposals.length > 0;
}

protected abstract ICompletionProposal[] buildProposals(ITextViewer viewer, int offset);

/**
 * Looks if proposals have already been initialized by a call to initializeProposals(...).
 * If not, build the proposals. 
 * 
 */
@Override
public final ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
	if (currentProposals == null)
		currentProposals = buildProposals(viewer, offset);
	ICompletionProposal[] proposals = currentProposals;
	currentProposals = null;
	return proposals;
}

}
