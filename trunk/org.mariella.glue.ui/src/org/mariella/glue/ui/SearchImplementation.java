package org.mariella.glue.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mariella.glue.service.QueryResult;
import org.mariella.rcp.ControlFactory;

public abstract class SearchImplementation <T, E> {
	protected ControlFactory controlFactory;
	protected Composite composite;
	protected final T queryParameter;
	
public SearchImplementation(T queryParameter) {
	super();
	this.queryParameter = queryParameter;
}

@SuppressWarnings("unchecked")
public E search(Shell parentShell, String title) {
	SearchDialog dialog = new SearchDialog(parentShell, title);
	dialog.setImplementation(this);
	if(dialog.open() == Window.OK) {
		return (E)dialog.getSelection();
	} else {
		return null;
	}	
}

public ControlFactory getControlFactory() {
	return controlFactory;
}

public Composite getComposite() {
	return composite;
}

public void setControlFactory(ControlFactory controlFactory) {
	this.controlFactory = controlFactory;
}

public void setComposite(Composite composite) {
	this.composite = composite;
}

protected abstract QueryResult<E> search();
protected abstract String getColumnText(Object element, int columnIndex);
protected abstract Image getColumnImage(Object element, int columnIndex);
protected abstract void addTableColumns(TableViewer tableViewer);

public T getSearchParameter() {
	return queryParameter;
}

public abstract void decorateComposite();

protected void createSeparator() {
	Label label = controlFactory.createSeparator(composite, SWT.HORIZONTAL);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 4;
	gd.verticalIndent = 10;
	gd.verticalAlignment = SWT.CENTER;
	label.setLayoutData(gd);
}

public void dispose() {
	
}

}
