package org.mariella.rcp.databinding.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public interface VContentAssistProcessor extends IContentAssistProcessor {

/**
 * Receiver's job is to build the list of completion proposals and 
 * return true if proposals are available, otherwise return false.
 * 
 * @param viewer
 * @param offset
 * @return
 */
boolean initializeProposals(ITextViewer viewer, int offset);

}
