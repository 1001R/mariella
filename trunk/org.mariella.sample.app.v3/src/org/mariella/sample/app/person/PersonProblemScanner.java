package org.mariella.sample.app.person;

import org.mariella.rcp.problems.Problem;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemManager;

public class PersonProblemScanner {

public void scanForProblems(ProblemManager problemMgr, PersonProblemResource problemResource, PersonResource personResource) {
	if (personResource.getPerson().getFirstName() == null || personResource.getPerson().getFirstName().trim().length() < 2) {
		problemMgr.addProblem(new Problem(problemResource, null, ProblemCategory.ERROR, "The first name must have at least 2 characters."));
	}
	if (personResource.getPerson().getLastName() == null || personResource.getPerson().getLastName().trim().length() < 2) {
		problemMgr.addProblem(new Problem(problemResource, null, ProblemCategory.ERROR, "The last name must have at least 2 characters."));
	}
}

}
