package org.mariella.rcp.problems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.mariella.rcp.resources.EditorOpenedCallback;
import org.mariella.rcp.resources.VManagedSelectionItem;
import org.mariella.rcp.resources.VResourceChangeEvent;
import org.mariella.rcp.resources.VResourceChangeListener;
import org.mariella.rcp.resources.VResourceSelectionEvent;
import org.mariella.rcp.resources.VResourceSelectionListener;


public class ProblemManager implements VResourceSelectionListener, VResourceChangeListener {
	
private List<ProblemsProvider> providers = new ArrayList<ProblemsProvider>();
private List<Problem> problemList = new ArrayList<Problem>();
private List<ProblemManagerObserver> observers = new ArrayList<ProblemManagerObserver>();
private boolean scanningProblems = false;
private int problemListVersion = -1;
private ProblemResourceHolder selectedProblemResourceHolder = null;

public ProblemManager() {
}

public void addProblem(Problem problem) {
	problemList.add(problem);
}

public void invalidate() {
	primInvalidate(null);
}

public void checkResourceErrors(ProblemResource resource) throws ResourceErrorsException {
	invalidate(resource);
	if (hasErrors(resource))
		throw new ResourceErrorsException(resource);
}

private void primInvalidate(final ProblemsProvider providerOrNull) {
	Job job = new Job(Messages.getString("ProblemManager.analyzingProblems")) { //$NON-NLS-1$
		@Override
		public IStatus run(IProgressMonitor monitor) {
			scanningProblems = true;
			try {
				synchronized(ProblemManager.this) {
					monitor.beginTask(Messages.getString("ProblemManager.analyzingProblems"), (providerOrNull != null ? 1 : providers.size())+2); //$NON-NLS-1$
					if (providerOrNull == null)
						problemList.clear();
					else
						removeProblemsOfProvider(providerOrNull);
					List<ProblemResource> problemResources = new ArrayList<ProblemResource>();
					for (ProblemsProvider provider : providers) {
						if (providerOrNull != null && provider != provider)
							continue;
						problemResources.addAll(provider.getProblemResources());
					}
					monitor.worked(1);
					for (ProblemResource res : problemResources) {
						monitor.setTaskName(Messages.getString("ProblemManager.searchingProblemsFor") + res.getDescription()); //$NON-NLS-1$
						res.getProvider().addProblems(ProblemManager.this, res);
						monitor.worked(1);
					}
				}
				
				monitor.setTaskName(Messages.getString("ProblemManager.Refreshing")); //$NON-NLS-1$
				UIJob uiJob  = new UIJob("RefreshingProblemsJob") { //$NON-NLS-1$
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
							public void run() {
								fireChanged();
							}
						});
						return Status.OK_STATUS;
					}
				};
				uiJob.setUser(true);
				uiJob.schedule();
				monitor.done();
				problemListVersion++;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				scanningProblems = false;
			}
			return Status.OK_STATUS;
		}
	};
	job.setUser(true);
	job.schedule();
}

public void invalidate(final ProblemResource res) {
	if (scanningProblems) return;
	synchronized(ProblemManager.this) {
		removeProblemsOfResource(res);
		res.getProvider().addProblems(ProblemManager.this, res);
	}
	fireChanged();
}

public void invalidate(final ProblemsProvider provider) {
	if (scanningProblems) return;
	primInvalidate(provider);
}


public void removed(final ProblemResource res) {
	if (scanningProblems) return;
	Display.getCurrent().asyncExec(new Runnable() {
		public void run() {
			removeProblemsOfResource(res);
			fireChanged();
		}
	});
}

private List<Problem> removeProblemsOfResource(ProblemResource res) {
	List<Problem> problems = new ArrayList<Problem>();
	for (Iterator<Problem> i = problemList.iterator(); i.hasNext();) {
		Problem t = i.next();
		if (t.getResource().equals(res)) {
			i.remove();
		}
	}
	return problems;
}

private List<Problem> removeProblemsOfProvider(ProblemsProvider provider) {
	List<Problem> problems = new ArrayList<Problem>();
	for (Iterator<Problem> i = problemList.iterator(); i.hasNext();) {
		Problem t = i.next();
		if (t.getResource().getProvider().equals(provider)) {
			i.remove();
		}
	}
	return problems;
}

public void openEditorAndSetSelection(final Problem problem) {
	ProblemResource res = problem.getResource();
	
    String editorId = res.getEditorId();
	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	try {
		EditorOpenedCallback cb = new EditorOpenedCallback() {
			public void editorOpened(final IEditorPart part) {
				if (part != null && problem.getSelection() != null)
					Display.getCurrent().asyncExec(new Runnable() {
						public void run() {
							part.getSite().getSelectionProvider().setSelection(problem.getSelection());
						}
					});
			}
		};
		implementOpenEditor(window, res, editorId, cb);
	} catch (PartInitException e) {
		throw new RuntimeException(e);
	}
}

private void implementOpenEditor(IWorkbenchWindow window, ProblemResource res, String editorId, EditorOpenedCallback cb) throws PartInitException {
	IEditorInput editorInput = getEditorInput(res);
	if (res.getResourceOpenHandler() != null)
		res.getResourceOpenHandler().openEditor(window, editorInput, editorId, cb);
	else {
		IEditorPart part = window.getActivePage().openEditor(getEditorInput(res), editorId);
		cb.editorOpened(part);
	}
}

private IEditorInput getEditorInput(ProblemResource res) {
	String elementFactoryId = res.getElementFactoryId();
	IMemento editorMemento = res.getEditorMemento();
    IElementFactory factory = PlatformUI.getWorkbench().getElementFactory(elementFactoryId);
    IEditorInput input = (IEditorInput)factory.createElement(editorMemento);
	return input;
}

protected void fireChanged() {
	for (ProblemManagerObserver o : observers)
		o.problemListChanged(this);
}

public void addProvider(ProblemsProvider p) {
	providers.add(p);
}

public void removeProvider(ProblemsProvider p) {
	providers.remove(p);
}

public void addObserver(ProblemManagerObserver o) {
	observers.add(o);
	o.problemListChanged(this);
	
}

public void removeObserver(ProblemManagerObserver o) {
	observers.remove(o);
}

public List<Problem> getProblemList() {
	return problemList;
}

public int getProblemListVersion() {
	return problemListVersion;
}

public boolean hasErrors(ProblemResource oroblemResource) {
	for (Iterator<Problem> i = problemList.iterator(); i.hasNext();) {
		Problem t = i.next();
		if (t.category == ProblemCategory.ERROR && t.getResource().equals(oroblemResource)) {
			return true;
		}
	}
	return false;
}

public void selectionChanged(VResourceSelectionEvent event) {
	ProblemResourceHolder newProblemResourceHolder = getFirstProblemResourceHolder(event.getSelection().getSelectedItems());
	if (newProblemResourceHolder != null) {
		selectedProblemResourceHolder = newProblemResourceHolder; 
		fireSelectedProblemResourceChanged();
	}
}

private ProblemResourceHolder getFirstProblemResourceHolder(List<VManagedSelectionItem> selection) {
	for (VManagedSelectionItem item : selection)
		if (item instanceof ProblemResourceHolder)
			return (ProblemResourceHolder)item;
	return null;
}

private void fireSelectedProblemResourceChanged() {
	for (ProblemManagerObserver o : observers)
		o.selectedProblemResourceChanged(getSelectedProblemResource());
}

public ProblemResource getSelectedProblemResource() {
	return selectedProblemResourceHolder == null ? null : selectedProblemResourceHolder.getProblemResource();
}

public void close() {}

public void resourceChanged(VResourceChangeEvent event) {
	if (event.getResource() instanceof ProblemResourceHolder)
		invalidate(((ProblemResourceHolder)event.getResource()).getProblemResource());
}

public void resourceRemoved(VResourceChangeEvent event) {
	if (event.getResource() instanceof ProblemResourceHolder)
		removed(((ProblemResourceHolder)event.getResource()).getProblemResource());
}

public void resourceLoaded(VResourceChangeEvent event) {
	if (event.getResource() instanceof ProblemResourceHolder)
		invalidate(((ProblemResourceHolder)event.getResource()).getProblemResource());
}

public List<Problem> getProblemsOfResource(ProblemResource resource) {
	List<Problem> result = new ArrayList<Problem>();
	for (Problem p : problemList)
		if (p.getResource().equals(resource))
			result.add(p);
	return result;
}

}
