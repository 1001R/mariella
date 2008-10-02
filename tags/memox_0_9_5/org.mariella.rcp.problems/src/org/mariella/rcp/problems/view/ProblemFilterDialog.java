package org.mariella.rcp.problems.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemsPlugin;


public class ProblemFilterDialog extends Dialog {

private Button showAllProblemsRadio;
private Button showProblemsOfSelectedResourceRadio;
private Button showErrorsCheckbox;
private Button showWarningsCheckbox;
private ProblemsControl control;

public ProblemFilterDialog(Shell parentShell, ProblemsControl control) {
	super(parentShell);
	this.control = control;
}

@Override
protected void okPressed() {
	control.preferences.showAllProblems = showAllProblemsRadio.getSelection();
	control.preferences.shownCategories.clear();
	if (showErrorsCheckbox.getSelection())
		control.preferences.shownCategories.add(ProblemCategory.ERROR);
	if (showWarningsCheckbox.getSelection())
		control.preferences.shownCategories.add(ProblemCategory.WARNING);
	control.preferences.save();
	super.okPressed();
}

@Override
protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText(Messages.getString("ProblemFilterDialog.filters")); //$NON-NLS-1$
	newShell.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(ProblemsPlugin.PLUGIN_ID, "icons/problems/filter.gif").createImage()); //$NON-NLS-1$
}

@Override
protected Control createDialogArea(Composite parent) {
	Composite composite = new Composite(parent, SWT.NONE);
	FormLayout layout = new FormLayout();
	layout.marginTop = 5;
	layout.marginLeft = 10;
	layout.marginRight = 10;
	layout.spacing = 5;
	composite.setLayout(layout);
	
	Composite primaryFilterComposite = createPrimaryFilterComposite(composite);
	FormData formData = new FormData();
	formData.left = new FormAttachment(0);
	formData.top = new FormAttachment(0);
	formData.right = new FormAttachment(100);
	primaryFilterComposite.setLayoutData(formData);

	Composite categoryFilterComposite = createCategoryFilterComposite(composite);
	formData = new FormData();
	formData.left = new FormAttachment(0);
	formData.top = new FormAttachment(primaryFilterComposite);
	formData.right = new FormAttachment(100);
	categoryFilterComposite.setLayoutData(formData);

	return composite;
}


private Composite createCategoryFilterComposite(Composite composite) {
	Group group = new Group(composite, SWT.NONE);
	group.setText(Messages.getString("ProblemFilterDialog.problemCategory")); //$NON-NLS-1$
	group.setLayout(new GridLayout());
	
	showWarningsCheckbox = new Button(group, SWT.CHECK);
	showWarningsCheckbox.setText(Messages.getString("ProblemFilterDialog.showWarnings")); //$NON-NLS-1$
	showWarningsCheckbox.setSelection(control.preferences.shownCategories.contains(ProblemCategory.WARNING));
	showErrorsCheckbox = new Button(group, SWT.CHECK);
	showErrorsCheckbox.setText(Messages.getString("ProblemFilterDialog.showErrors")); //$NON-NLS-1$
	showErrorsCheckbox.setSelection(control.preferences.shownCategories.contains(ProblemCategory.ERROR));
	
	return group;
}

private Composite createPrimaryFilterComposite(Composite composite) {
	Group group = new Group(composite, SWT.NONE);
	group.setText(Messages.getString("ProblemFilterDialog.general")); //$NON-NLS-1$
	group.setLayout(new GridLayout());
	
	showAllProblemsRadio = new Button(group, SWT.RADIO);
	showAllProblemsRadio.setText(Messages.getString("ProblemFilterDialog.showAllProblems")); //$NON-NLS-1$
	showAllProblemsRadio.setSelection(control.preferences.showAllProblems);
	showProblemsOfSelectedResourceRadio = new Button(group, SWT.RADIO);
	showProblemsOfSelectedResourceRadio.setText(Messages.getString("ProblemFilterDialog.showProblemsOfSelection")); //$NON-NLS-1$
	showProblemsOfSelectedResourceRadio.setSelection(!control.preferences.showAllProblems);
	
	return group;
}

}
