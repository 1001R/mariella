package org.mariella.rcp.problems;

import org.eclipse.ui.IWorkbenchWindow;
import org.mariella.rcp.resources.ResourceOpenedCallback;

public interface ProblemResourceOpenHandler {

/**
 * Returns null if the editor cannot be opened for some reason
 * 
 * @param editorInput
 * @param editorId
 * @return
 */
void openResource(IWorkbenchWindow window, ProblemResource problemResource, ResourceOpenedCallback cb);

}
