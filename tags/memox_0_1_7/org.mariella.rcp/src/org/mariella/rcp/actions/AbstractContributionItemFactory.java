package org.mariella.rcp.actions;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class AbstractContributionItemFactory {

private final String contributionItemId;

protected AbstractContributionItemFactory(String contributionItemId) {
    this.contributionItemId = contributionItemId;
}

public abstract IContributionItem create(IWorkbenchWindow window, ActionFactoryCallback callback);

public IContributionItem create(IWorkbenchWindow window) {
	return create(window, null);
}

public String getId() {
	return contributionItemId;
}

}
