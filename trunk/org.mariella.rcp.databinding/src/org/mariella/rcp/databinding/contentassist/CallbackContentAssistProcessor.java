package org.mariella.rcp.databinding.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

public class CallbackContentAssistProcessor extends AbstractContentAssistProcessor {

class ProposalImpl extends AbstractCompletionProposal {
Image image;
ProposalImpl(String value, Image image, int replacementOffset, int replacementLength) {
	super(value, replacementOffset, replacementLength, value.length());
	this.image = image;
}
@Override
public Image getImage() {
	return image;
}
}

public class ProposalsBuilder {
ITextViewer textViewer;
String word;
int offset;
int replacementOffset;
List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
String curWord = null;
Image curImage = null;
public void setWord(String word) {
	this.curWord = word;
}
public void setImage(Image image) {
	this.curImage = image;
}
public void addProposal() {
	ProposalImpl proposal = new ProposalImpl(curWord, curImage, replacementOffset, word.length());
	proposals.add(proposal);
	curWord = null;
	curImage  = null;
}
ICompletionProposal[] build() {
	IDocument doc = textViewer.getDocument();
	word = doc.get().substring(0, offset);
	replacementOffset = offset-word.length();
	callback.addEntries(this, word);
	ICompletionProposal[] array = new ICompletionProposal[proposals.size()];
	return proposals.toArray(array);
}
}

public interface Callback {
void addEntries(ProposalsBuilder builder, String word);	
}

private Callback callback;

public CallbackContentAssistProcessor(Callback callback) {
	this.callback = callback;
}

public boolean hasProposals(ITextViewer viewer, int offset) {
	return computeCompletionProposals(viewer, offset).length > 0;
}

public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
	ProposalsBuilder builder = new ProposalsBuilder();
	builder.textViewer = viewer;
	builder.offset = offset;
	return builder.build();
}

}
