package org.mariella.sample.app;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.mariella.sample.app.person.PersonsView;

public class Perspective implements IPerspectiveFactory {

public void createInitialLayout(IPageLayout layout) {
	String editorArea = layout.getEditorArea();
	layout.setEditorAreaVisible(true);
	layout.setFixed(false);
	
	IFolderLayout left = layout.createFolder(
            "left", IPageLayout.LEFT, (float) 0.4,//$NON-NLS-1$
            editorArea);

	IFolderLayout bottom = layout.createFolder(
            "bottom", IPageLayout.BOTTOM, (float) 0.8,//$NON-NLS-1$
            editorArea);

	left.addView(PersonsView.ID);

}
}
