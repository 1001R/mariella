package org.mariella.rcp.problems;

import org.mariella.rcp.resources.VResourceSaveException;

public class ResourceSaveErrorsException extends VResourceSaveException {
private static final long serialVersionUID = 1L;

public ResourceSaveErrorsException(ProblemResource resource) {
	super("Errors exist for the resource '" + resource.getDescription() + "'.\n\nLook at the Problems View and correct the errors.");
}

}
