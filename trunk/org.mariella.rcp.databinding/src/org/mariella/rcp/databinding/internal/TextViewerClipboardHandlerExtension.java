package org.mariella.rcp.databinding.internal;

import org.mariella.rcp.databinding.BindingDomainExtension;
import org.mariella.rcp.databinding.VBinding;

public class TextViewerClipboardHandlerExtension implements BindingDomainExtension {

public void install(VBinding binding) {
	if (!(binding.getBinding().getTarget() instanceof VTextViewerObservableValue)) return;
	
	VTextViewerObservableValue value = (VTextViewerObservableValue)binding.getBinding().getTarget();
	value.installClipboardHandlerExtension(this);
}

}
