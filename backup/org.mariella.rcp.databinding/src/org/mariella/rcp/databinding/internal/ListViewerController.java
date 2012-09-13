package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.mariella.rcp.databinding.ListViewerFontExtension;
import org.mariella.rcp.databinding.ListViewerImageExtension;
import org.mariella.rcp.databinding.ListViewerLabelDecoratorExtension;
import org.mariella.rcp.databinding.ListViewerLabelExtension;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.VBindingContext;

public class ListViewerController extends StructuredViewerController implements ILabelProvider, IFontProvider {

private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
VBindingContext bindingContext;
AbstractListViewer listViewer;
ListViewerLabelExtension labelExtension = null;
ListViewerImageExtension imageExtension = null;
ListViewerFontExtension fontExtension = null;
ListViewerLabelDecoratorExtension labelDecoratorExtension = null;

public static ListViewerController createListViewerController(VBindingContext dbc, AbstractListViewer listViewer) {
	ListViewerController controller = new ListViewerController();
	controller.bindingContext = dbc;
	((InternalBindingContext)dbc).getMainContext().listViewerControllerMap.put(listViewer, controller);
	controller.setListViewer(listViewer);
	return controller;
}

private void setListViewer(AbstractListViewer listViewer) {
	this.listViewer = listViewer;
	listViewer.setLabelProvider(this);
}

@Override
public Image getImage(Object element) {
	if (imageExtension != null)
		return getImage(imageExtension.getImageCallback().getImageDescriptor(element));
	return null;
}

private Image getImage(ImageDescriptor imageDescriptor) {
	if (imageDescriptor == null) return null;
	return (Image) resourceManager.get(imageDescriptor);
}

@Override
public String getText(Object element) {
	if (labelExtension == null) return "";
	
	PropertyPathSupport propertyPathSupport = new PropertyPathSupport();
	propertyPathSupport.object = element;
	propertyPathSupport.propertyPath = labelExtension.getPropertyPath();
	propertyPathSupport.initialize();
	Object value =  propertyPathSupport.implementDoGetValue();
	
	Object converted = labelExtension.getDomain().getConverterBuilder().buildFromModelConverter(labelExtension.getDomain()).convert(value);
	String string =  (converted == null ? "" : converted.toString());
	if (labelDecoratorExtension == null) return string;
	return labelDecoratorExtension.getLabelDecoratorCallback().decorateLabel(element, string);
}

@Override
public void addListener(ILabelProviderListener listener) {}

@Override
public void dispose() {}

@Override
public boolean isLabelProperty(Object element, String property) {
	return false;
}

@Override
public void removeListener(ILabelProviderListener listener) {}

@Override
public Font getFont(Object element) {
	if (fontExtension != null)
		return fontExtension.getFontCallback().getFont(element);
	return null;
}

public void install(ListViewerImageExtension imageExtension) {
	this.imageExtension = imageExtension;
}

public void install(ListViewerFontExtension fontExtension) {
	this.fontExtension = fontExtension;
}

public void install(ListViewerLabelExtension labelExtension, VBinding binding) {
	this.labelExtension = labelExtension;
	if (labelExtension.getDomain() == null) {
		labelExtension.setDomain(binding.getBindingContext().getBindingFactory().getDomainRegistry().getDomain(labelExtension.getDomainSymbol()));
	}
}

public void install(ListViewerLabelDecoratorExtension labelDecoratorExtension, VBinding binding) {
	this.labelDecoratorExtension = labelDecoratorExtension;
}

@Override
public void extensionsInstalled() {}

@Override
public Collection<String> getPropertyPathes() {
	List<String> pathes = new ArrayList<String>(1);
	if (labelExtension != null)
		pathes.add(labelExtension.getPropertyPath());
	return pathes;
}

@Override
public boolean hookElementChangeListeners() {
	return true;
}

}
