package org.mariella.rcp.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class VResourceSelectionManager implements ISelectionListener, IPartListener {

private List<VManagedSelectionItem> lastSelection;
private List<VManagedSelectionItem> selection;
private List<VResourceSelectionListener> selectionListeners = new ArrayList<VResourceSelectionListener>();
private IWorkbenchPart part;
private boolean pageOpened = false;

public VResourceSelectionManager(IWorkbenchWindow window) {
	window.getSelectionService().addSelectionListener(this);
	window.addPageListener(new IPageListener() {
		public void pageOpened(IWorkbenchPage page) {
			pageOpened = true;
			page.addPartListener(VResourceSelectionManager.this);
		}
		public void pageClosed(IWorkbenchPage page) {
			page.removePartListener(VResourceSelectionManager.this);
		}
		public void pageActivated(IWorkbenchPage page) {
		}
	});
}

public void addSelectionListener(VResourceSelectionListener l) {
	selectionListeners.add(l);
	if (part != null && selection != null) {
		VResourceSelectionEvent event = new VResourceSelectionEvent(part, new VResourceSelection(part, selection));
		l.selectionChanged(event);
	}
}

public void removeSelectionListener(VResourceSelectionListener l) {
	selectionListeners.remove(l);
}

public void selectionChanged(IWorkbenchPart part, ISelection iSelection) {
	if (part instanceof IEditorPart && selection != null && selection.contains(((IEditorPart)part).getEditorInput()))
		// a) if some editor holds the enabled state
		// b) and the selection change event comes from this editor
		// -> we ignore the event
		return;
	this.selection = null; 
	if (iSelection instanceof IStructuredSelection) {
		IStructuredSelection structuredSelection = (IStructuredSelection)iSelection;
		this.part = part;
		this.selection = new ArrayList<VManagedSelectionItem>();
		Iterator items = structuredSelection.iterator();
		while (items.hasNext()) {
			Object item = items.next();
			if (item instanceof VManagedSelectionItem) {
				this.selection.add((VManagedSelectionItem)item);
			}
		}
	}
	fireSelectionChanged();
}

public void partActivated(IWorkbenchPart part) {
	if (part instanceof IEditorPart) {
		selection = null;
		IEditorPart editorPart = (IEditorPart)part;
		IEditorInput input = editorPart.getEditorInput();
		if (input instanceof VManagedSelectionItem) {
			this.part = part;
			this.selection= new ArrayList<VManagedSelectionItem>();
			this.selection.add((VManagedSelectionItem)input);
		}
		fireSelectionChanged();
	}
}

public void partDeactivated(IWorkbenchPart part) {
	if (part instanceof IEditorPart)	{	
		selection = null;
		fireSelectionChanged();
	}
}

public void partBroughtToTop(IWorkbenchPart part) {
}

public void partClosed(IWorkbenchPart part) {
}


public void partOpened(IWorkbenchPart part) {
}

private void fireSelectionChanged() {
	if (hasSelectionChanged()) {
		lastSelection = selection == null ? null : new ArrayList<VManagedSelectionItem>(selection);
		VResourceSelectionEvent event = new VResourceSelectionEvent(part,  new VResourceSelection(part, selection == null ? Collections.EMPTY_LIST : selection));
		for (VResourceSelectionListener l : selectionListeners)
			l.selectionChanged(event);
	}
}

private boolean hasSelectionChanged() {
	if (lastSelection == null) return selection != null;
	return !lastSelection.equals(selection);
}

/**
 * Patch for editors opened and activated at startup
 * 
 * @param editor
 */
public void retreivedFocus(IEditorPart editor) {
	if (pageOpened) return;
	
	partActivated(editor);
}

public void setSelection(VManagedSelectionItem item) {
	selection = new ArrayList<VManagedSelectionItem>();
	selection.add(item);
	
	fireSelectionChanged();
}

public List<VManagedSelectionItem> getSelection() {
	return selection;
}

public IWorkbenchPart getPart() {
	return part;
}

}
