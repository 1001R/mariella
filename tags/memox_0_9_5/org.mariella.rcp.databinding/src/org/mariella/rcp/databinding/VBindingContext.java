package org.mariella.rcp.databinding;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;

public interface VBindingContext {

void addObserver(VBindingContextObserver observer);

ISelectionProvider getSelectionProvider();

VBindingFactory getBindingFactory();

ISelectionProvider getDataBindingSelectionProvider();

void dispose();

void updateTargets();

void setDelegateSelectionProvider(ISelectionProvider delegateProvider);

VBindingContext createSubBindingContext();

void setActionBars(IActionBars actionBars);

}
