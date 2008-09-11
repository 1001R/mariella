package org.mariella.glue.ui;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.mariella.glue.service.Context;
import org.mariella.glue.service.QueryResult;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.forms.ControlFactory;
import org.mariella.rcp.forms.DialogControlFactory;

import at.hts.persistence.runtime.BeanInfo;
import at.hts.persistence.runtime.Introspector;
import at.hts.persistence.schema.ClassDescription;

public abstract class SearchDialog <T, E> extends Dialog {
	private Action searchAction = new Action("Search") {
		public void run() {
			Context context = createContext();
			QueryResult<E> result = search(context);
			setSearchResult(result);
		};
	};

	private String title;
	protected TableViewer tableViewer;
	
	protected T searchParameter;
	protected VBindingContext bindingContext;
	protected ControlFactory controlFactory;
	protected SearchCompositeDecorator<T> decorator;
	protected QueryResult<E> queryResult;

	protected E selection;

public SearchDialog(Shell parentShell, T searchParameter, String title) {
	super(parentShell);
	this.searchParameter = searchParameter;
	this.title = title;
}

protected abstract SearchCompositeDecorator<T> createDecorator(Composite composite); 
protected abstract QueryResult<E> search(Context context);
protected abstract Context createContext();
protected abstract VBindingContext createBindingContext();
protected abstract String getColumnText(E element, int columnIndex);
protected abstract Image getColumnImage(E element, int columnIndex);
protected abstract void addTableColumns();

protected void setSearchResult(QueryResult<E> queryResult) {
	this.queryResult = queryResult;
	initializeTableViewer();
	tableViewer.setInput(queryResult);
}

protected void initializeTableViewer() {
	for(TableColumn tc : tableViewer.getTable().getColumns()) {
		tc.dispose();
	}
	if(queryResult != null && !queryResult.getResult().isEmpty()) {
		addTableColumns();
	}
	tableViewer.getTable().setHeaderVisible(true);
	tableViewer.refresh();
}

@Override
protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText(title);
}

@Override
protected Control createDialogArea(Composite parent) {
	bindingContext = createBindingContext();
	controlFactory = new DialogControlFactory();
	
	Composite composite = new Composite(parent, SWT.None);
	composite.setLayout(new FillLayout(SWT.VERTICAL));
	
	GridData gd;
	Group group;
	
	group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	group.setText("Search Conditions");
	GridLayout layout = new GridLayout(4, false);
	group.setLayout(layout);

	decorator = createDecorator(group);
	decorator.setSearchParameter(searchParameter);
	decorator.decorateComposite();
	
	Composite buttonComposite = new Composite(group, SWT.None);
	buttonComposite.setLayout(new FillLayout());
	gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
	gd.horizontalSpan = 4;
	buttonComposite.setLayoutData(gd);
	new ActionContributionItem(searchAction).fill(buttonComposite);
	
	group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	group.setText("Search Result");
	group.setLayout(new FillLayout());
	
	tableViewer = new TableViewer(group, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	tableViewer.setContentProvider(createContentProvider());
	tableViewer.setLabelProvider(createLabelProvider());
	
	tableViewer.addSelectionChangedListener(
		new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				tableSelectionChanged();
			}
		}
	);
	
	tableViewer.addDoubleClickListener(
		new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
			}
		}
	);
	bindingContext.updateTargets();
	
	return composite;
}

@Override
protected void createButtonsForButtonBar(Composite parent) {
	super.createButtonsForButtonBar(parent);
	tableSelectionChanged();
}

protected void tableSelectionChanged() {
	getButton(IDialogConstants.OK_ID).setEnabled(!tableViewer.getSelection().isEmpty());
}

protected Object getValue(E element, String propertyName) {
	BeanInfo bi = Introspector.Singleton.getBeanInfo(element.getClass());
	try {
		PropertyDescriptor pd = bi.getPropertyDescriptor(propertyName);
		return pd.getReadMethod().invoke(element, new Object[] {});
	} catch(Exception e) {
		throw new RuntimeException(e);
	}
}

@Override
@SuppressWarnings("unchecked")
protected void okPressed() {
	selection = (E)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
	super.okPressed();
}

public E getSelection() {
	return selection;
}

protected IStructuredContentProvider createContentProvider() {
	return new IStructuredContentProvider() {
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	
		@Override
		public void dispose() {
		}
	
		@Override
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			return inputElement == null ? null : ((QueryResult<E>)inputElement).getResult().toArray();
		}
	};
}

protected ITableLabelProvider createLabelProvider() { 
	return new ITableLabelProvider() {
		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return true;
		}
	
		@Override
		public void dispose() {
		}
	
		@Override
		public void addListener(ILabelProviderListener listener) {
		}
	
		@Override
		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex) {
			return SearchDialog.this.getColumnText((E)element, columnIndex);
		}
	
		@Override
		@SuppressWarnings("unchecked")
		public Image getColumnImage(Object element, int columnIndex) {
			return SearchDialog.this.getColumnImage((E)element, columnIndex);
		}
	};
}

}