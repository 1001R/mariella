package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.ISelectionProvider;

public interface VBindingContext {

ISelectionProvider getSelectionProvider();

VBindingFactory getBindingFactory();

ISelectionProvider getDataBindingSelectionProvider();

void dispose();

void updateTargets();

void setDelegateSelectionProvider(ISelectionProvider delegateProvider);


}
