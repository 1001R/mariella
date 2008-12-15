package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ConverterLabelProvider implements ILabelProvider {

IConverter converter;

public ConverterLabelProvider(IConverter converter) {
	this.converter = converter;
}

public Image getImage(Object element) {
	return null;
}

public String getText(Object element) {
	return (String)converter.convert(element);
}

public void addListener(ILabelProviderListener listener) {
}

public void dispose() {
}

public boolean isLabelProperty(Object element, String property) {
	return true;
}

public void removeListener(ILabelProviderListener listener) {
}

}
