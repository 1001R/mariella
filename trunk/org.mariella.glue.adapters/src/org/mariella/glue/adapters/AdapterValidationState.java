package org.mariella.glue.adapters;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.mariella.rcp.adapters.Adapter;
import org.mariella.rcp.adapters.AdapterContext;
import org.mariella.rcp.adapters.AdapterContextObserver;
import org.mariella.rcp.problems.Problem;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemManager;
import org.mariella.rcp.problems.ProblemManagerObserver;
import org.mariella.rcp.problems.ProblemResource;
import org.mariella.rcp.problems.ProblemResourceOpenHandler;
import org.mariella.rcp.problems.ProblemsPlugin;
import org.mariella.rcp.problems.ProblemsProvider;

public abstract class AdapterValidationState implements ProblemsProvider, ProblemManagerObserver, AdapterContextObserver {
	
	private ProblemResource problemResource = new ProblemResource() {
	@Override
	public ProblemResourceOpenHandler getResourceOpenHandler() {
		return null;
	}

	@Override
	public ProblemsProvider getProvider() {
		return AdapterValidationState.this;
	}

	@Override
	public String getDescription() {
		return null;
	}
	};
	
	private String errorText = null;
	private boolean valid = false;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private Adapter adapter;

	
public AdapterValidationState(Adapter adapter) {
	this.adapter = adapter;
	ProblemsPlugin.getProblemManager().addProvider(this);
	ProblemsPlugin.getProblemManager().addObserver(this);
	adapter.getAdapterContext().addObserver(this);
	validate();
}


/**
 * API-User is responsible for invoking dispose if the holder of this object is disposed
 */
public void dispose() {
	ProblemsPlugin.getProblemManager().removeProvider(this);
	ProblemsPlugin.getProblemManager().removeObserver(this);
	adapter.getAdapterContext().removeObserver(this);
}

public String getErrorText() {
	return errorText;
}

public void setErrorText(String errorText) {
	Object oldValue = getErrorText();
	this.errorText = errorText;
	propertyChangeSupport.firePropertyChange("errorText", oldValue, errorText);
}

@Override
public List<ProblemResource> getProblemResources() {
	List<ProblemResource> resources = new ArrayList<ProblemResource>(1);
	resources.add(problemResource);
	return resources;
}

@Override
public void problemListChanged(ProblemManager mgr) {
	List<Problem> problems = mgr.getProblemsOfResource(problemResource);
	String errorText = fetchErrorText(problems);
	setErrorText(errorText);
	setValid(problems.isEmpty());
}

/**
 * Default is to take the first problem of the known list.
 * 
 * @param problems
 * @return
 */
protected String fetchErrorText(List<Problem> problems) {
	return problems.size() == 0 ? null : problems.get(0).getDescription();
}


@Override
public final void selectedProblemResourceChanged(ProblemResource problemResource) {}


public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
}


public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
}


public boolean isValid() {
	return valid;
}


public void setValid(boolean valid) {
	Object oldValue = isValid();
	this.valid = valid;
	propertyChangeSupport.firePropertyChange("valid", oldValue, valid);
}

public Problem createProblem() {
	Problem problem = new Problem();
	problem.setCategory(ProblemCategory.ERROR);
	problem.setResource(problemResource);
	ProblemsPlugin.getProblemManager().addProblem(problem);
	return problem;
}


public boolean validate() {
	ProblemsPlugin.getProblemManager().invalidate(problemResource);
	return isValid();
}

@Override
public void dirtyNotification(AdapterContext context) {
	ProblemsPlugin.getProblemManager().invalidate(problemResource);
}

public List<Problem> getProblems() {
	return ProblemsPlugin.getProblemManager().getProblemsOfResource(problemResource);
}

}
