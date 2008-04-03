package org.mariella.rcp.databinding;

import org.eclipse.jface.resource.ImageDescriptor;

public abstract class ImageDecoratorExtension implements VBindingDomainExtension {

public void install(VBinding binding) {
	// currently the image decorator extension is only available in the table-context
}

public abstract ImageDescriptor getImageDescriptor(Object value);

}
