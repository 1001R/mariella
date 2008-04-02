package org.mariella.sample.app.person;

import org.mariella.rcp.problems.VResourceProblemResource;
import org.mariella.rcp.resources.VResourcesPlugin;

public class PersonProblemResource extends VResourceProblemResource {

public PersonProblemResource(PersonResourceRef ref, String description) {
	super(
			VResourcesPlugin.getResourceManagerRegistry().getResourceManager(PersonResourceManager.class),	// our ProblemsProvider  
			ref,		// the given VResourceRef
			PersonEditor.ID,		// the corresponding Eclipse editor id 
			PersonResourceElementFactory.ID,	// the corresponding Eclipse IElementFactory id
			description);	// the given text that appears in the ProblemsView
}

}
