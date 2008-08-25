package org.mariella.rcp.problems.view;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;


public class ProblemsView extends ViewPart {
public final static String ID = ProblemsView.class.getName();


private ProblemsControl control;


public ProblemsView() {
}

@Override
public void createPartControl(Composite parent) {
	control = new ProblemsControl(parent, SWT.NONE) {
		
	};
	
	hookContextMenu();
	initializeToolbar();
}

private void hookContextMenu() {
	MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
	menuMgr.setRemoveAllWhenShown(true);
	menuMgr.addMenuListener(new IMenuListener() {
		public void menuAboutToShow(IMenuManager manager) {
			ProblemsView.this.fillContextMenu(manager);
		}
	});
	Menu menu = menuMgr.createContextMenu(control.treeViewer.getControl());
	control.treeViewer.getControl().setMenu(menu);
	getSite().registerContextMenu(menuMgr, control.treeViewer);
}

private void initializeToolbar() {
	IActionBars bars = getViewSite().getActionBars();
	fillLocalPullDown(bars.getMenuManager());
	fillLocalToolBar(bars.getToolBarManager());
}

private void fillLocalPullDown(IMenuManager manager) {
	manager.add(control.refreshAction);
	manager.add(control.openFilterAction);
}

private void fillContextMenu(IMenuManager manager) {
	manager.add(control.refreshAction);
	manager.add(control.openFilterAction);
	// Other plug-ins can contribute there actions here
	manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
}

private void fillLocalToolBar(IToolBarManager manager) {
	manager.add(control.refreshAction);
	manager.add(control.openFilterAction);
}

@Override
public void setFocus() {
	control.treeViewer.getTree().setFocus();
}

}
