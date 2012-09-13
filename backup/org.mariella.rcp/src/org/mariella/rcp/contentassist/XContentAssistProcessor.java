package org.mariella.rcp.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public interface XContentAssistProcessor extends IContentAssistProcessor {

boolean hasProposals(ITextViewer viewer, int offset);

}
