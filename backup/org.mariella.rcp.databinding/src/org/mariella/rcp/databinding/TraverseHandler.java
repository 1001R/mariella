package org.mariella.rcp.databinding;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;


/**
 * Handles traverse order in the order of the bindings 
 * 
 * @author maschmid
 *
 */
public class TraverseHandler {

public TraverseHandler() {
}

public void incrementFocusControl(Control control, int direction) {
	control.traverse(direction == 1 ? SWT.TRAVERSE_TAB_NEXT  : SWT.TRAVERSE_TAB_PREVIOUS);
}

}
