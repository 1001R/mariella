package org.mariella.rcp.databinding.internal;

import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.VBindingDomainExtension;

public class TextViewerClipboardHandlerExtension implements VBindingDomainExtension {

public void install(VBinding binding) {
	if (!(binding.getBinding().getTarget() instanceof VTextViewerObservableValue)) return;
	
	VTextViewerObservableValue value = (VTextViewerObservableValue)binding.getBinding().getTarget();
	value.installClipboardHandlerExtension(this);
}

}
