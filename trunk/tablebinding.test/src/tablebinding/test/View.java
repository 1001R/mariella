package tablebinding.test;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.mariella.rcp.ControlFactory;
import org.mariella.rcp.DialogControlFactory;
import org.mariella.rcp.databinding.PassingConverterBuilder;
import org.mariella.rcp.databinding.TableViewerColumnEditExtension;
import org.mariella.rcp.databinding.TableViewerColumnEditExtensionCallback;
import org.mariella.rcp.databinding.TableViewerColumnExtension;
import org.mariella.rcp.databinding.TableViewerEditExtension;
import org.mariella.rcp.databinding.TextBindingDetails;
import org.mariella.rcp.databinding.TextViewerFocusBehaviourExtension;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainRegistry;
import org.mariella.rcp.databinding.VBindingFactory;

public class View extends ViewPart {
	public static final String ID = "tablebinding.test.view";
	
	public static class Entry {
		String name;
		boolean show;
		
		public Entry(String name, boolean show) {
			this.name = name;
			this.show = show;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isShow() {
			return show;
		}

		public void setShow(boolean show) {
			this.show = show;
		}
	}
	
	
	VBindingFactory bindingFactory;
	VBindingContext bindingContext;
	ControlFactory controlFactory = new DialogControlFactory();
	private TableViewer tableViewer;
	
	private IObservableList entries;
	
	private boolean showAll = true;


	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		
		bindingFactory = new VBindingFactory(new VBindingDomainRegistry());
		bindingContext = bindingFactory.createBindingContext();
		
		entries = bindingFactory.createObservableList(bindingContext);
		entries.add(new Entry("A", true));
		entries.add(new Entry("B", false));
		entries.add(new Entry("C", true));

		
		
		Composite client = controlFactory.createComposite(parent);
		GridLayout gridLayout = new GridLayout(1, false);
		client.setLayout(gridLayout);
	
		GridData gridData;
		
		Composite controls = controlFactory.createComposite(client);
		controls.setLayout(new FillLayout());
		Button showAll = controlFactory.createButton(controls, "show all", SWT.CHECK);
		bindingFactory.createButtonBinding(bindingContext, showAll, this, "showAll", new VBindingDomain("Boolean", Boolean.class));
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		controls.setLayoutData(gridData);
		
		Button doImp = controlFactory.createButton(controls, "Import", SWT.PUSH);
		doImp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doImportJob();
			}
		});
		
		createTable(client);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace=true;
		tableViewer.getControl().setLayoutData(gridData);
	}
	
	void doImportJob() {
		final UIJobRealm realm = new UIJobRealm(Display.getCurrent());
		Job job = new Job("My Import Job") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("blah", 100);
				for (int i=0; i<100; i++) {
					if (monitor.isCanceled()) return Status.CANCEL_STATUS;
					monitor.setTaskName("Importing Rec# " + i);
					final int x = i;
					realm.exec(new Runnable() {
						@Override
						public void run() {
							entries.add(new Entry("X" + x, true));
						}
					});
					monitor.worked(1);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						return Status.CANCEL_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	private void createTable(Composite parent) {
		tableViewer = new TableViewer(controlFactory.createTable(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL));
		
		tableViewer.setFilters(new ViewerFilter[] {
				new ViewerFilter() {
					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						Entry entry = (Entry)element;
						if (showAll) return true;
						return entry.isShow();
					}
				}
		});
		
		final VBindingDomain nameDomain = new VBindingDomain("String", String.class, new PassingConverterBuilder());
		
		bindingContext.getBindingFactory().createTableViewerListBinding(bindingContext, 
				tableViewer, 
				this, "entries",  //$NON-NLS-1$
				new VBindingDomain("entries",  //$NON-NLS-1$
						Entry.class,
						new TableViewerEditExtension(),
						new TableViewerColumnExtension("name", nameDomain, "Name", 30),
						new TableViewerColumnEditExtension("name", new TableViewerColumnEditExtensionCallback() {
							@Override
							public Control createEditControl(IObservableValue selectionHolder, Composite parent) {
								TextViewer textViewer = controlFactory.createTextViewer(parent, SWT.SINGLE);
								
								bindingFactory.createTextBinding(bindingContext, 
										textViewer, 
										selectionHolder, "name",  //$NON-NLS-1$
										nameDomain.copyExtend(new TextViewerFocusBehaviourExtension()),
										new TextBindingDetails(SWT.Modify));
								
								return textViewer.getControl();
							}
						})
						));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	public IObservableList getEntries() {
		return entries;
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
		tableViewer.refresh();
	}
}