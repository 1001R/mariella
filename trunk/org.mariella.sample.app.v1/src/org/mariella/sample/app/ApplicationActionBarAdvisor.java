package org.mariella.sample.app;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
private IWorkbenchAction exitAction;
private IWorkbenchAction saveAction;
private IWorkbenchAction saveAllAction;
private IWorkbenchAction closeAction;
private IWorkbenchAction closeAllAction;
private IContributionItem openViews;

public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
    super(configurer);
}

protected void makeActions(IWorkbenchWindow window) {
	exitAction = ActionFactory.QUIT.create(window);
	register(exitAction);
	closeAction = ActionFactory.CLOSE.create(window);
	register(closeAction);
	closeAllAction = ActionFactory.CLOSE_ALL.create(window);
	register(closeAllAction);
	saveAction = ActionFactory.SAVE.create(window);
	register(saveAction);
	saveAllAction = ActionFactory.SAVE_ALL.create(window);
	register(saveAllAction);
	openViews = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
}

protected void fillMenuBar(IMenuManager menuBar) {
	MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
	menuBar.add(fileMenu);
	fileMenu.add(closeAction);
	fileMenu.add(closeAllAction);
	fileMenu.add(new Separator());
	fileMenu.add(saveAction);
	fileMenu.add(saveAllAction);
	fileMenu.add(new Separator());
	fileMenu.add(exitAction);
	
	MenuManager view = new MenuManager("View", "view");
	menuBar.add(view);
	view.add(openViews);
}
    
}