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

public class MultiSearchDialog extends AbstractSearchDialog {
	protected SearchImplementation<?, ?>[] implementations;
	protected TabFolder tabFolder;
	
public MultiSearchDialog(Shell shell, String title) {
	super(shell, title);
}

public SearchImplementation<?, ?>[] getImplementations() {
	return implementations;
}

public void setImplementations(SearchImplementation<?, ?>[] implementations) {
	this.implementations = implementations;
}

@Override
public SearchImplementation<?, ?> getImplementation() {
	return implementations[tabFolder.getSelectionIndex()];
}

@Override
public boolean close() {
	boolean result = super.close();
	for(SearchImplementation<?, ?> implementation : implementations) {
		implementation.dispose();
	}
	return result;
}

@Override
protected Control createDialogArea(Composite parent) {
	controlFactory = new DialogControlFactory();
	
	Composite composite = new Composite(parent, SWT.None);
	composite.setLayout(new FillLayout(SWT.VERTICAL));

	
	tabFolder = new TabFolder(composite, SWT.None);
	Group group;
	
	for(SearchImplementation<?, ?> implementation : implementations) {
		TabItem tabItem = new TabItem(tabFolder, SWT.None);
		tabItem.setText(implementation.getLabel());
		
		GridData gd;
		
		group = new Group(tabFolder, SWT.SHADOW_ETCHED_IN);
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
		
		tabItem.setControl(group);
	}
	
	tabFolder.setSelection(0);
	
	group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	group.setText("Search Result");
	group.setLayout(new FillLayout());
	
	createTableViewer(group);
	
	return composite;
}

}
