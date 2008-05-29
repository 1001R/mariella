package org.mariella.rcp.problems.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.mariella.rcp.problems.Problem;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemManager;
import org.mariella.rcp.problems.ProblemManagerObserver;
import org.mariella.rcp.problems.ProblemResource;
import org.mariella.rcp.problems.ProblemsPlugin;
import org.mariella.rcp.util.TreeLayout;


public class ProblemsView extends ViewPart implements ProblemManagerObserver {
public final static String ID = ProblemsView.class.getName();


class RefreshWorker extends Thread {
long start = System.currentTimeMillis();
long refreshRequest = -1L;
boolean stopRequest;
@Override
public void run() {
	while (!stopRequest) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}
		boolean needsRefresh = false;
		synchronized(this) {
			needsRefresh = refreshRequest != -1L && System.currentTimeMillis() > refreshRequest + 600;
		}
		if (needsRefresh) {
			synchronized(this) {
				refreshRequest = -1L;
			}
			new UIJob("RefreshProblemsView") {
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					refreshTree(getProblemManager());
					return Status.OK_STATUS;
				}
			}.schedule();
		}			
	}
}
}

static class RefreshAction extends Action {
public static final String ID = RefreshAction.class.getName();
ProblemsView view;
public RefreshAction(ProblemsView view) {
	this.view = view;
	setText("Aktualisieren");
	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ProblemsPlugin.PLUGIN_ID, "icons/problems/refresh.gif"));
}

@Override
public void run() {
	view.getProblemManager().invalidate();
}

public void dispose() {}
}



static class OpenFilterDialogAction extends Action {
public static final String ID = OpenFilterDialogAction.class.getName();
ProblemsView view;
public OpenFilterDialogAction(ProblemsView view) {
	this.view = view;
	setText("Filter konfigurieren");
	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ProblemsPlugin.PLUGIN_ID, "icons/problems/filter.gif"));
}

@Override
public void run() {
	ProblemFilterDialog filterDialog = new ProblemFilterDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), view);
	if (filterDialog.open() == Window.OK)
		view.refreshTree(view.getProblemManager());
}

public void dispose() {}
}


class LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof ProblemCategory)
				return (Image)resourceManager.get(AbstractUIPlugin.imageDescriptorFromPlugin(ProblemsPlugin.PLUGIN_ID, "icons/problems/category.gif"));
			else if (element instanceof Problem) {
				Problem problem = (Problem)element;
				switch(problem.getCategory()) {
				case ERROR:
					return (Image)resourceManager.get(AbstractUIPlugin.imageDescriptorFromPlugin(ProblemsPlugin.PLUGIN_ID, "icons/problems/error.gif"));
				case WARNING:
					return (Image)resourceManager.get(AbstractUIPlugin.imageDescriptorFromPlugin(ProblemsPlugin.PLUGIN_ID, "icons/problems/warning.gif"));
				}
			}
		}
			
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			if (element instanceof ProblemCategory) {
				ProblemCategory cat = (ProblemCategory)element;
				switch (cat) {
				case ERROR:
					return "Fehler";
				case WARNING:
					return "Warnungen";
				}
				throw new IllegalArgumentException();
			} else {
				Problem problem = (Problem)element;
				return problem.getDescription();
			}
		case 1:
			if (element instanceof Problem)
				return ((Problem)element).getResource().getDescription();
			else
				return "";
		}
		throw new IllegalArgumentException();
	}

	public void addListener(ILabelProviderListener listener) {}

	public void dispose() {}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {}
}


class TreeContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		ProblemCategory cat = (ProblemCategory)parentElement;
		return shownProblems.get(cat).toArray();
	}

	public Object getParent(Object element) {
		if (element instanceof Problem)
			return ((Problem)element).getCategory();
		else
			return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof ProblemCategory;
	}

	public Object[] getElements(Object inputElement) {
		List<ProblemCategory> cats =new ArrayList<ProblemCategory>(shownProblems.keySet());
		Collections.sort(cats, new Comparator<ProblemCategory>() {
			public int compare(ProblemCategory o1, ProblemCategory o2) {
				return o1.getDefaultOrder().compareTo(o2.getDefaultOrder());
			}
		});
		return cats.toArray();
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}

protected TreeViewer treeViewer;
private RefreshAction refreshAction;
private OpenFilterDialogAction openFilterAction;
Map<ProblemCategory, List<Problem>> shownProblems = new HashMap<ProblemCategory, List<Problem>>();
private int lastProblemListVersion = -1;
ProblemsViewPreferences preferences;
private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
private RefreshWorker refreshWorker;
public ProblemsView() {
	//getSite().getWorkbenchWindow().getActivePage().addPartListener(this);

	makeActions();
	loadPreferences();
	refreshWorker = new RefreshWorker();
}

private void loadPreferences() {
	preferences = new ProblemsViewPreferences();
	preferences.load();
}

private void makeActions() {
	refreshAction = new RefreshAction(this);
	
	openFilterAction = new OpenFilterDialogAction(this);
}

@Override
public void createPartControl(Composite parent) {
	Tree tree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	
	TreeLayout layout = new TreeLayout();
	tree.setLayout(layout);
	//tree.setLinesVisible(true);
	
	TreeColumn description = new TreeColumn(tree, SWT.NONE);
	description.setText("Beschreibung");

	TreeColumn resourceDescription = new TreeColumn(tree, SWT.NONE);
	resourceDescription.setText("Resource");

	tree.setHeaderVisible(true);
	layout.addColumnData(new ColumnWeightData(60, 30, true));
	layout.addColumnData(new ColumnWeightData(40, 30, true));

	
	// create TreeViewer
	treeViewer = new TreeViewer(tree);
	// attach content provider
	treeViewer.setContentProvider(new TreeContentProvider());
	// attach label provider
	treeViewer.setLabelProvider(new LabelProvider());
	// set input

	hookSelectionListeners();
	hookContextMenu();
	initializeToolbar();
	
	refreshWorker.start();
}

@Override
public void dispose() {
	super.dispose();
	synchronized(refreshWorker) {
		refreshWorker.stopRequest = true;
	}
	getProblemManager().removeObserver(this);
}

private void hookSelectionListeners() {
	treeViewer.addDoubleClickListener(new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			if (selection.size() == 0) return;

			Problem problem = (Problem)selection.getFirstElement();
			getProblemManager().openEditorAndSetSelection(problem);
		}
	});
	
	getProblemManager().addObserver(this);
	if (getProblemManager().getProblemListVersion() != lastProblemListVersion)
		refreshTree(getProblemManager());
}



private void hookContextMenu() {
	MenuManager menuMgr = new MenuManager("#PopupMenu");
	menuMgr.setRemoveAllWhenShown(true);
	menuMgr.addMenuListener(new IMenuListener() {
		public void menuAboutToShow(IMenuManager manager) {
			ProblemsView.this.fillContextMenu(manager);
		}
	});
	Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
	treeViewer.getControl().setMenu(menu);
	getSite().registerContextMenu(menuMgr, treeViewer);
}

private void initializeToolbar() {
	IActionBars bars = getViewSite().getActionBars();
	fillLocalPullDown(bars.getMenuManager());
	fillLocalToolBar(bars.getToolBarManager());
}

private void fillLocalPullDown(IMenuManager manager) {
	manager.add(refreshAction);
	manager.add(openFilterAction);
}

private void fillContextMenu(IMenuManager manager) {
	manager.add(refreshAction);
	manager.add(openFilterAction);
	// Other plug-ins can contribute there actions here
	manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
}

private void fillLocalToolBar(IToolBarManager manager) {
	manager.add(refreshAction);
	manager.add(openFilterAction);
}


void refreshTree(final ProblemManager mgr) {
	if (treeViewer.getTree().isDisposed()) return;
	
	lastProblemListVersion = mgr.getProblemListVersion(); 
	buildShownProblems(mgr);
	treeViewer.setInput(shownProblems);
	treeViewer.expandAll();
}

private void buildShownProblems (ProblemManager mgr) {
	List<Problem> problems = new ArrayList<Problem>();
	for (Problem t : mgr.getProblemList()) {
		if (!preferences.shownCategories.contains(t.getCategory())) continue;
		if (preferences.showAllProblems || t.getResource().equals(mgr.getSelectedProblemResource()))
			problems.add(t);
	}
	shownProblems.clear();
	for (Problem t2 : problems) {
		List<Problem> problemsOfCat = shownProblems.get(t2.getCategory());
		if (problemsOfCat == null) {
			problemsOfCat = new ArrayList<Problem>();
			shownProblems.put(t2.getCategory(), problemsOfCat);
		}
		problemsOfCat.add(t2);
	}
}

@Override
public void setFocus() {
	treeViewer.getTree().setFocus();
}

public void selectedProblemResourceChanged(ProblemResource problemResource) {
	if (!preferences.showAllProblems)
		refreshTree(getProblemManager());
}

public void problemListChanged(ProblemManager mgr) {
	synchronized(refreshWorker) {
		refreshWorker.refreshRequest = System.currentTimeMillis();
		//refreshWorker.notify();
	}
}

private ProblemManager getProblemManager() {
	return ProblemsPlugin.getProblemManager();
}

}
