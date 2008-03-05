package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.databinding.ComboViewerLabelProviderExtension;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.ValueSetExtension;

public class VComboViewerObservableValue extends VStructuredViewerSingleSelectionObservableValue implements SelectionAwareObservable, EnabledObservableValueFactory, ILabelProvider, VTargetObservable,
	ValueSetAwareObservable {

private SelectionDispatchingObservableSupport selectionDispatchingSupport;
private ComboViewerLabelProviderExtension labelProviderExtension;
private ValueSetExtension valueSetExtension = null;

public VComboViewerObservableValue(IObservableValue nested, ComboViewer comboViewer) {
	super(nested, comboViewer);
	initialize();
}

public VComboViewerObservableValue(IObservableValue nested, ComboViewer comboViewer, Class targetType) {
	super(nested, comboViewer, targetType);
	initialize();
}

private ComboViewer getComboViewer()  {
	return (ComboViewer)getStructuredViewer();
}

private void initialize() {
	selectionDispatchingSupport = new SelectionDispatchingObservableSupport(this, new ControlSelectionDecorator(getComboViewer().getControl())); 
}

protected void doSetValue(Object value) {
	if (valueSetExtension != null) {
		applyValueSet();
	}
	super.doSetValue(value);
}

public boolean dispatchSelectionPath(SelectionPath path, int offset) {
	return selectionDispatchingSupport.implementDispatchSelectionPath(path, offset);
}

public VDataBindingSelection getSelection() {
	Combo combo = getComboViewer().getCombo(); 
	if (combo != null && !combo.isDisposed() && combo.isFocusControl())
		return selectionDispatchingSupport.implementGetSelection();
	return null;
}

public void setSelectionBasePath(Object[] path) {
	selectionDispatchingSupport.implementSetSelectionBasePath(path);
}

public EnabledObservableValue createEnabledObservableValue() {
	return new DefaultEnabledObservableValue(getComboViewer().getCombo());
}

public void setSelectionTargetControl(Control control) {
	selectionDispatchingSupport.setSelectionTargetControl(control);
}

public void installLabelProviderExtension(ComboViewerLabelProviderExtension extension, VBinding binding) {
	if (extension.getDomain() == null)
		extension.setDomain(binding.getDataBindingContext().getDataBindingFactory().getDomainRegistry().getDomain(extension.getDomainSymbol()));
	this.labelProviderExtension = extension;
	getComboViewer().setLabelProvider(this);
}

private Object readLabelProviderValue(Object element) {
	PropertyPathSupport propertyPathSupport = new PropertyPathSupport();
	propertyPathSupport.object = element;
	propertyPathSupport.propertyPath = labelProviderExtension.getPropertyPath();
	propertyPathSupport.initialize();
	return propertyPathSupport.implementDoGetValue();
}

public Image getImage(Object element) {
	return null;
}

public String getText(Object element) {
	Object value = readLabelProviderValue(element);
	Object converted = labelProviderExtension.getDomain().getConverterBuilder().buildFromModelConverter(
			labelProviderExtension.getDomain()).convert(value);
	return (converted == null ? "" : converted.toString());
}

public void addListener(ILabelProviderListener listener) {}

public boolean isLabelProperty(Object element, String property) {
	return false;
}

public void removeListener(ILabelProviderListener listener) {}

public void extensionsInstalled() {
}

public void setContextSelectionCallback(	GetContextSelectionCallback getContextSelectionCallback) {
	selectionDispatchingSupport.setContextSelectionCallback(getContextSelectionCallback);
}

public void installValueSetExtension(ValueSetExtension ext) {
	this.valueSetExtension = ext;
	getComboViewer().setContentProvider(new DefaultListContentProvider());
	applyValueSet();
}

private void applyValueSet() {
	List newValueSet = valueSetExtension.getValueSet();
	List oldValueSet = (List)getComboViewer().getInput();
	if (oldValueSet == null || Diffs.computeListDiff(oldValueSet, newValueSet).getDifferences().length > 0)
		getComboViewer().setInput(new ArrayList(newValueSet));
}

}
