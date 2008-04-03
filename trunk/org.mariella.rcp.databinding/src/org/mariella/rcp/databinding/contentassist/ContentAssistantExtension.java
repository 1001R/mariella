package org.mariella.rcp.databinding.contentassist;

import org.mariella.rcp.databinding.VBindingDomainExtension;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.internal.VTextViewerObservableValue;

public abstract class ContentAssistantExtension implements VBindingDomainExtension {

public void install(VBinding binding) {
	VTextViewerObservableValue textViewerObservable = (VTextViewerObservableValue)binding.getBinding().getTarget(); 
	ContentAssistantController controller = new ContentAssistantController(textViewerObservable.getTextViewer(), this, binding.getDomain().getDomainContext());
	controller.installContentAssistant();
	textViewerObservable.setContentAssistantController(controller);
}

protected abstract VContentAssistProcessor createContentAssistProcessor(Object domainContext);

}
