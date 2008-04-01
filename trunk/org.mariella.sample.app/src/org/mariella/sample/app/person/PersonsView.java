package org.mariella.sample.app.person;

import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mariella.rcp.resources.VResourcesPlugin;
import org.mariella.sample.core.Person;
import org.mariella.sample.core.SampleCorePlugin;

public class PersonsView extends ViewPart {
public static final String ID = PersonsView.class.getName();

/**
 * Content provider for table viewer
 * 
 * @author maschmid
 *
 */
class ViewContentProvider implements IStructuredContentProvider {
@Override
public Object[] getElements(Object inputElement) {
	return ((List<Person>)inputElement).toArray();
}
@Override
public void dispose() {}
@Override
public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}

/**
 * Label providdeer for table viewer
 * 
 * @author maschmid
 *
 */
class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
@Override
public String getColumnText(Object element, int columnIndex) {
	Person person = (Person)element;
	return person.getLastName();
}
@Override
public Image getColumnImage(Object element, int columnIndex) {
	return null;
}
}

TableViewer tableViewer;

@Override
public void createPartControl(Composite parent) {
	tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
	tableViewer.setContentProvider(new ViewContentProvider());
	tableViewer.setLabelProvider(new ViewLabelProvider());
	
	tableViewer.addDoubleClickListener(new IDoubleClickListener() {
		@Override
		public void doubleClick(DoubleClickEvent event) {
			if (tableViewer.getSelection().isEmpty()) return;
			Person person = (Person)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
			PersonResourceManager rm = VResourcesPlugin.getResourceManagerRegistry().getResourceManager(PersonResourceManager.class);
			rm.openEditor(getSite().getWorkbenchWindow(), person.getId());
		}
	});
	
	refreshContent();
}

void refreshContent() {
	tableViewer.setInput(SampleCorePlugin.getCoreService().getAvailablePersons());
}

@Override
public void setFocus() {
	tableViewer.getControl().setFocus();
}

}
