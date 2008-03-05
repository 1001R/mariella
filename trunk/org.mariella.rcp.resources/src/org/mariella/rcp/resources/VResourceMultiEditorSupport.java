package org.mariella.rcp.resources;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;

public class VResourceMultiEditorSupport extends AbstractVResourceEditorSupport {

public VResourceMultiEditorSupport(VResourceEditorPart editorPart) {
	super(editorPart);
}

public void implementCreatePages() {
	((VResourceMultiEditorCustomizationCallback)customizationCallback).implementCreatePages();
	refresh(false);
}

public void implementInit(IEditorSite site, IEditorInput input) throws PartInitException {
	super.implementInit(site, input);
	editorPart.getSite().setSelectionProvider(new MultiPageSelectionProvider((MultiPageEditorPart)editorPart));
}

}
