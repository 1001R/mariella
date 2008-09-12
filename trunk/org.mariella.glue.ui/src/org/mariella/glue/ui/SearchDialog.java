package org.mariella.glue.ui;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.mariella.rcp.DialogControlFactory;

public class SearchDialog extends AbstractSearchDialog {
	protected SearchImplementation<?, ?> implementation;

public SearchDialog(Shell parentShell, String title) {
	super(parentShell, title);
}

public SearchImplementation<?, ?> getImplementation() {
	return implementation;
}

public void setImplementation(SearchImplementation<?, ?> implementation) {
	this.implementation = implementation;
}

@Override
public boolean close() {
	boolean result = super.close();
	implementation.dispose();
	return result;
}

@Override
protected Control createDialogArea(Composite parent) {
	controlFactory = new DialogControlFactory();
	
	Composite composite = new Composite(parent, SWT.None);
	composite.setLayout(new FillLayout(SWT.VERTICAL));
	
	GridData gd;
	Group group;
	
	group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	group.setText("Search Conditions");
	GridLayout layout = new GridLayout(4, false);
	group.setLayout(layout);

	implementation.setControlFactory(controlFactory);
	implementation.setComposite(group);
	implementation.decorateComposite();
	
	Composite buttonComposite = new Composite(group, SWT.None);
	buttonComposite.setLayout(new FillLayout());
	gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
	gd.horizontalSpan = 4;
	buttonComposite.setLayoutData(gd);
	new ActionContributionItem(searchAction).fill(buttonComposite);
	
	group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	group.setText("Search Result");
	group.setLayout(new FillLayout());
	
	createTableViewer(group);
	
	return composite;
}

}