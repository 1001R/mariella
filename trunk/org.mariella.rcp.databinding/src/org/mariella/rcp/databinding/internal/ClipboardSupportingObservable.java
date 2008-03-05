package org.mariella.rcp.databinding.internal;

public interface ClipboardSupportingObservable {
	
boolean canCut();
boolean canCopy();
boolean canPaste();
boolean canDelete();
void doCut();
void doCopy();
void doPaste();
void doDelete();

}
