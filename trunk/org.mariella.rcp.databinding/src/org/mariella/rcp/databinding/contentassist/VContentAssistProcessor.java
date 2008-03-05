package org.mariella.rcp.databinding.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public interface VContentAssistProcessor extends IContentAssistProcessor {

boolean hasProposals(ITextViewer viewer, int offset);

}
