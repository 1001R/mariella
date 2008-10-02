package org.mariella.rcp.problems;

import org.eclipse.jface.viewers.ISelection;

public class Problem {

ProblemCategory category;
ISelection selection;
String description;
ProblemResource resource;


public Problem(ProblemResource resource, ISelection selection, ProblemCategory category, String description) {
	super();
	this.category = category;
	this.selection = selection;
	this.description = description;
	this.resource = resource;
}


public Problem() {
}


public ProblemCategory getCategory() {
	return category;
}


public void setCategory(ProblemCategory category) {
	this.category = category;
}


public ISelection getSelection() {
	return selection;
}


public void setSelection(ISelection selection) {
	this.selection = selection;
}


public String getDescription() {
	return description;
}


public void setDescription(String description) {
	this.description = description;
}


public ProblemResource getResource() {
	return resource;
}


public void setResource(ProblemResource resource) {
	this.resource = resource;
}

}
