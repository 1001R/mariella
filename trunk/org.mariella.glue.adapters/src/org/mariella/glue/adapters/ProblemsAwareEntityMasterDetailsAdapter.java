package org.mariella.glue.adapters;

import java.util.ArrayList;
import java.util.List;

import org.mariella.glue.service.Entity;
import org.mariella.rcp.adapters.AdapterContext;
import org.mariella.rcp.adapters.AdapterContextObserver;
import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBindingSelection;
import org.mariella.rcp.problems.Problem;
import org.mariella.rcp.problems.ProblemManager;
import org.mariella.rcp.problems.ProblemManagerObserver;
import org.mariella.rcp.problems.ProblemResource;
import org.mariella.rcp.problems.ProblemResourceOpenHandler;
import org.mariella.rcp.problems.ProblemsPlugin;
import org.mariella.rcp.problems.ProblemsProvider;

public abstract class ProblemsAwareEntityMasterDetailsAdapter<E extends Entity> extends EntityMasterDetailsAdapter<E> 
	implements ProblemsProvider, ProblemManagerObserver, AdapterContextObserver {

	private ProblemResource problemResource = new ProblemResource() {
		@Override
		public ProblemResourceOpenHandler getResourceOpenHandler() {
			return null;
		}
	
		@Override
		public ProblemsProvider getProvider() {
			return ProblemsAwareEntityMasterDetailsAdapter.this;
		}
	
		@Override
		public String getDescription() {
			return null;
		}
	};
	
	public class ProblemScanContext {
		public E currentEntity;
		public int currentEntityIndex;
		ProblemManager problemManager;
	}
	
	private String errorText = null;
	
public ProblemsAwareEntityMasterDetailsAdapter(EntityMasterDetailsAdapterContext<E> context) {
	super(context);
	ProblemsPlugin.getProblemManager().addProvider(this);
	ProblemsPlugin.getProblemManager().addObserver(this);
	context.addObserver(this);
}

@Override
public void addProblems(ProblemManager problemMgr, ProblemResource resource) {
	ProblemScanContext ctx = new ProblemScanContext();
	ctx.problemManager = problemMgr;
	int index=0;
	for (E entity : getDetailsList()) {
		ctx.currentEntity = entity;
		ctx.currentEntityIndex = index++;
		scanForProblems(ctx);
	}
}

protected void scanForProblems(ProblemScanContext ctx) {}

protected Problem createProblem(ProblemScanContext context) {
	Problem problem = new Problem();
	problem.setResource(problemResource);
	context.problemManager.addProblem(problem);
	return problem;
}

protected VBindingSelection buildSelection(ProblemScanContext context, Object ... baseQualifiers) {
	SelectionPath[] path = new SelectionPath[] {
			new SelectionPath(new SelectionPath(baseQualifiers), 
					context.currentEntityIndex)
	};
	return new VBindingSelection(path);
}

@Override
public List<ProblemResource> getProblemResources() {
	ArrayList<ProblemResource> resources = new ArrayList<ProblemResource>();
	resources.add(problemResource);
	return resources;
}

@Override
public void dispose() {
	ProblemsPlugin.getProblemManager().removeObserver(this);
	ProblemsPlugin.getProblemManager().removeProvider(this);
	getEntityMasterDetailsAdapterContext().removeObserver(this);

	super.dispose();
}

@Override
public void problemListChanged(ProblemManager mgr) {
	List<Problem> problems = mgr.getProblemsOfResource(problemResource);
	String errorText = problems.size() == 0 ? null : problems.get(0).getDescription();
	setErrorText(errorText);
}

public String getErrorText() {
	return errorText;
}

public void setErrorText(String errorText) {
	Object oldValue = getErrorText();
	this.errorText = errorText;
	firePropertyChange("errorText", oldValue, errorText);
}

@Override
public void dirtyNotification(AdapterContext context) {
	ProblemsPlugin.getProblemManager().invalidate(problemResource);
}

@Override
public void selectedProblemResourceChanged(ProblemResource problemResource) {}

}
