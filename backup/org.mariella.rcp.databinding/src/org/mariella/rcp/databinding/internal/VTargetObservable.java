package org.mariella.rcp.databinding.internal;

import org.eclipse.swt.widgets.Control;

public interface VTargetObservable {
	
void extensionsInstalled();

boolean isResponsibleFor(Control control);

/**
 * Implementors must return true if the default traverse behaviour
 * of its responsible control must be disabled.
 * 
 *  This can happen if for example a ContentAssistant is open.
 * 
 * @return
 */
boolean blockDefaultTraversing();

}
