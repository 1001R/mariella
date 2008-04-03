package org.mariella.rcp.databinding;

import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.databinding.internal.SelectionAwareObservable;
import org.mariella.rcp.databinding.internal.VDataBindingSelectionProvider;

public class SelectionManagementExtension implements VBindingDomainExtension {

Control targetControl;
Object[] rootQualifiers;

public SelectionManagementExtension(Object ...rootQualifiers) {
	this((Control)null, rootQualifiers);
}

public SelectionManagementExtension(Control targetControl, Object ...rootQualifiers) {
	this.targetControl = targetControl;
	this.rootQualifiers = rootQualifiers;
}

public SelectionManagementExtension(Object[] rootQualifiers1, Object[] rootQualifiers2) {
	this.rootQualifiers = new Object[rootQualifiers1.length + rootQualifiers2.length];
	System.arraycopy(rootQualifiers1, 0, rootQualifiers, 0, rootQualifiers1.length);
	System.arraycopy(rootQualifiers2, 0, rootQualifiers, rootQualifiers1.length, rootQualifiers2.length);
}

public void install(VBinding binding) {
	if (!(binding.getBinding().getTarget() instanceof SelectionAwareObservable)) {
		throw new IllegalStateException("SelectionManagementExtension can only be applied to bindings where the target observable implements the " + SelectionAwareObservable.class.getName() + " interface");
	}
	
	((VDataBindingSelectionProvider)binding.getDataBindingContext().getDataBindingSelectionProvider()).addManagedBinding(binding);
	
	((SelectionAwareObservable)binding.getBinding().getTarget()).setSelectionBasePath(rootQualifiers);
	
	if (targetControl != null)
		((SelectionAwareObservable)binding.getBinding().getTarget()).setSelectionTargetControl(targetControl);
}

}
