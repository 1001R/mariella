package org.mariella.glue.ui;

import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBindingSelection;
import org.mariella.rcp.problems.ProblemManager;

public class ScreeningProblemScanner {
	protected ProblemManager mgr;
	protected ScreeningProblemResource resource;

public ScreeningProblemScanner(ProblemManager mgr, ScreeningProblemResource resource) {
	super();
	this.mgr = mgr;
	this.resource = resource;
}

protected ScreeningProblem createProblem(SelectionPath path) {
	ScreeningProblem p = new ScreeningProblem();
	p.setResource(resource);
	p.setSelection(new VBindingSelection(path));
	mgr.addProblem(p);
	return p;
}

public void scanForProblems() {
	
}

}