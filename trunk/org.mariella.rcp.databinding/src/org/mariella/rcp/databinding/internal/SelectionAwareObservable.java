package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Control;
import org.mariella.rcp.databinding.SelectionPath;

public interface SelectionAwareObservable {

/**
 * Returns true if it was responsible for selection path and had dispatched it.
 * 
 * @param path
 * @return
 */
boolean dispatchSelectionPath(SelectionPath path, int offset);

void setSelectionBasePath(Object[] qualifiers);

/**
 * Returns a VDataBindingSelection instance or null if nothing is selected in the context of this observable.
 * 
 * @return
 */
VDataBindingSelection getSelection();

/**
 * If a targetControl is given to the selection management extension,
 * the receiver can keep it by implementing this method. 
 * 
 * @param control
 */
void setSelectionTargetControl(Control control);

void setContextSelectionCallback(GetContextSelectionCallback getContextSelectionCallback);
}
