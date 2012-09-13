package tablebinding.test;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		IFolderLayout folder = layout.createFolder(
	            "top", IPageLayout.TOP, 0.3f,//$NON-NLS-1$
	            editorArea);
		
		folder.addView(View2.ID);
		folder.addView(View.ID);
	}

}
