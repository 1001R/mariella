package tablebinding.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mariella.rcp.ControlFactory;
import org.mariella.rcp.DialogControlFactory;
import org.mariella.rcp.databinding.ListViewerLabelExtension;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainRegistry;
import org.mariella.rcp.databinding.VBindingFactory;

public class View2 extends ViewPart {
	public static final String ID = View2.class.getName();
	
	public static class Entry {
		String name;
		
		public Entry(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	
	VBindingFactory bindingFactory;
	VBindingContext bindingContext;
	ControlFactory controlFactory = new DialogControlFactory();
	private ComboViewer comboViewer;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private List<Entry> entries = null;
	private Entry selectedEntry = null;
	

/**
 * This is a callback that will allow us to create the viewer and initialize
 * it.
 */
public void createPartControl(Composite parent) {
	bindingFactory = new VBindingFactory(new VBindingDomainRegistry());
	bindingContext = bindingFactory.createBindingContext();
	
	Composite client = controlFactory.createComposite(parent);
	GridLayout gridLayout = new GridLayout(1, false);
	client.setLayout(gridLayout);

	GridData gridData;
	
	Composite controls = controlFactory.createComposite(client);
	controls.setLayout(new FillLayout());
	gridData = new GridData(GridData.FILL_HORIZONTAL);
	controls.setLayoutData(gridData);
	
	Button invalidateEntriesButton = controlFactory.createButton(controls, "Replace entries of Combo", SWT.PUSH);
	invalidateEntriesButton.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			invalidateEntries();
		}
	});
	
	comboViewer = controlFactory.createComboViewer(client, SWT.DROP_DOWN | SWT.READ_ONLY);
	gridData = new GridData();
	comboViewer.getControl().setLayoutData(gridData);
	
	bindingFactory.createListViewerListBinding(bindingContext, 
			comboViewer, 
			this, "entries", 
			new VBindingDomain(Entry.class,
					new ListViewerLabelExtension("name", new VBindingDomain(String.class))));
	
	bindingFactory.createSingleSelectionBinding(bindingContext, 
			comboViewer, 
			this, "selectedEntry", 
			new VBindingDomain(Entry.class));

}

/**
 * Passing the focus request to the viewer's control.
 */
public void setFocus() {
	comboViewer.getControl().setFocus();
}

public Entry getSelectedEntry() {
	if (selectedEntry == null) {
		selectedEntry = getEntries().get(0);
	}
	return selectedEntry;
}

public void setSelectedEntry(Entry selectedEntry) {
	this.selectedEntry = selectedEntry;
}

public List<Entry> getEntries() {
	if (entries == null) {
		entries = new ArrayList<Entry>();
		entries.add(new Entry("A"));
		entries.add(new Entry("B"));
		entries.add(new Entry("C"));

	}
	return entries;
}

protected void invalidateEntries() {
	entries = null;
	propertyChangeSupport.firePropertyChange("entries", null, getEntries());
}

public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
}

public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
}
}