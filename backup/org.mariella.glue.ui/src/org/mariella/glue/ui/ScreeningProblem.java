package org.mariella.glue.ui;

import org.eclipse.jface.viewers.ISelection;
import org.mariella.rcp.problems.Problem;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemResource;

public class ScreeningProblem extends Problem {

public ScreeningProblem(ProblemResource resource, ISelection selection, ProblemCategory category, String description) {
	super(resource, selection, category, description);
}

public ScreeningProblem() {
	super();
}
}
