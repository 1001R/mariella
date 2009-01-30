package org.mariella.rcp.databinding;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.InternalBindingContext;
import org.mariella.rcp.databinding.internal.PropertyPathSupport;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

public class TableViewerCheckboxExtension implements VBindingDomainExtension {

	// must point to a boolean or Boolean property
	String propertyPath;

public TableViewerCheckboxExtension(String propertyPath) {
	this.propertyPath = propertyPath;
}

@Override
public void install(VBinding binding) {
	VTableViewerObservableList target = getTargert(binding);
	Assert.isTrue(target.getTableViewer() instanceof CheckboxTableViewer, "You must use the " + CheckboxTableViewer.class.getName() + " viewer in order to use this extension" );
	
	TableController controller = ((InternalBindingContext)binding.getBindingContext()).getMainContext().tableControllerMap.get(target.getTableViewer());
	controller.install(this);

}

private VTableViewerObservableList getTargert(VBinding binding) {
	return (VTableViewerObservableList)binding.getBinding().getTarget();
}

public String getPropertyPath() {
	return propertyPath;
}

}
