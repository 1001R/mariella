package org.mariella.rcp.databinding;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.mariella.rcp.databinding.internal.PropertyPathSupport;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;

public class TableViewerCheckboxExtension implements VBindingDomainExtension {

	// must point to a boolean or Boolean property
	String propertyPath;

public TableViewerCheckboxExtension(String propertyPath) {
	this.propertyPath = propertyPath;
}

@Override
public void install(VBinding binding) {
	TableViewer viewer = getTableViewer(binding);
	Assert.isTrue(viewer instanceof CheckboxTableViewer, "You must use the " + CheckboxTableViewer.class.getName() + " in order to use this extension" );
	
	((CheckboxTableViewer)viewer).addCheckStateListener(new ICheckStateListener() {
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			PropertyPathSupport pathSupp = new PropertyPathSupport();
			pathSupp.propertyPath = propertyPath;
			pathSupp.object = event.getElement();
			pathSupp.initialize();
			pathSupp.implementDoSetValue(event.getChecked());
		}
	});
}

private TableViewer getTableViewer(VBinding binding) {
	return ((VTableViewerObservableList)binding.getBinding().getTarget()).getTableViewer();
}

}
