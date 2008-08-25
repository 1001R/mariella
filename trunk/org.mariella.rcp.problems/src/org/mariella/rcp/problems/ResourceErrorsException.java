package org.mariella.rcp.problems;

import org.mariella.rcp.resources.VResourceSaveException;

public class ResourceErrorsException extends VResourceSaveException {
private static final long serialVersionUID = 1L;

public ResourceErrorsException(ProblemResource resource) {
	super(Messages.getString("ResourceErrorsException.resourceErrorsForResourceExisting_1") + resource.getDescription() + Messages.getString("ResourceErrorsException.resourceErrorsForResourceExisting_2")); //$NON-NLS-1$ //$NON-NLS-2$
}

}
