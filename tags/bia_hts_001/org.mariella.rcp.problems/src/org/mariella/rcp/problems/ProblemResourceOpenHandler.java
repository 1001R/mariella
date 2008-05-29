package org.mariella.rcp.problems;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.mariella.rcp.resources.EditorOpenedCallback;

public interface ProblemResourceOpenHandler {

/**
 * Returns null if the editor cannot be opened for some reason
 * 
 * @param editorInput
 * @param editorId
 * @return
 */
void openEditor(IWorkbenchWindow window, IEditorInput editorInput, String editorId, EditorOpenedCallback cb);

}
