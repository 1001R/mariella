package org.mariella.rcp.databinding;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Display;
import org.mariella.rcp.databinding.internal.VTextViewerObservableValue;

public class TextViewerFocusBehaviourExtension implements VBindingDomainExtension {

public void install(final VBinding binding) {
	if (!(binding.getBinding().getTarget() instanceof VTextViewerObservableValue)) return;
	
	final ITextViewer textViewer = getTextViewer(binding);
	textViewer.getTextWidget().addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					if (textViewer != null && textViewer.getTextWidget() != null && !textViewer.getTextWidget().isDisposed()) { 
						textViewer.getTextWidget().invokeAction(ST.SELECT_ALL);
						binding.getBindingContext().globalClipboardActionsHandler.validateActions();
					}
				}
			});
		}
		@Override
		public void focusLost(FocusEvent e) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					if (textViewer != null && textViewer.getTextWidget() != null && !textViewer.getTextWidget().isDisposed()) {
						textViewer.getTextWidget().setSelection(0,0);
						binding.getBindingContext().globalClipboardActionsHandler.validateActions();
					}
				}
			});
		}
	});
}

private ITextViewer getTextViewer(VBinding binding) {
	return ((VTextViewerObservableValue)binding.getBinding().getTarget()).getTextViewer();
}

}
