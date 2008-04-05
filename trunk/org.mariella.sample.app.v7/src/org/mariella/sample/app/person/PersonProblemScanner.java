package org.mariella.sample.app.person;

import org.mariella.rcp.databinding.SelectionPath;
import org.mariella.rcp.databinding.VBindingSelection;
import org.mariella.rcp.problems.Problem;
import org.mariella.rcp.problems.ProblemCategory;
import org.mariella.rcp.problems.ProblemManager;
import org.mariella.sample.core.Address;

public class PersonProblemScanner {

public void scanForProblems(ProblemManager problemMgr, PersonProblemResource problemResource, PersonResource personResource) {
	if (personResource.getPerson().getFirstName() == null || personResource.getPerson().getFirstName().trim().length() < 2) {
		problemMgr.addProblem(new Problem(
				problemResource,	// the problem resource 
				new VBindingSelection(new SelectionPath(PersonEditorPage.ID, "firstName")),
				ProblemCategory.ERROR, 	// the problem category
				"The first name must have at least 2 characters."));	// the description that appears in the ProblemsView
	}
	if (personResource.getPerson().getLastName() == null || personResource.getPerson().getLastName().trim().length() < 2) {
		problemMgr.addProblem(new Problem(
				problemResource, 
				new VBindingSelection(new SelectionPath(PersonEditorPage.ID, "lastName")),
				ProblemCategory.ERROR, 
				"The last name must have at least 2 characters."));
	}
	
	for (Address address : personResource.getPerson().getAddresses()) {
		if (address.getStreet() == null || address.getStreet().length() < 5) {
			problemMgr.addProblem(new Problem(
					problemResource, 
					new VBindingSelection(new SelectionPath(AddressesEditorPage.ID, 
							"addresses",
							personResource.getPerson().getAddresses().indexOf(address),
							"street")),
					ProblemCategory.ERROR, 
					"The street must have at least 5 characters."));
		}
	}
}

}
