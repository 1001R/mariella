package at.hts.persistence.persistor;

import at.hts.persistence.schema.ClassDescription;

public class ClusterDescription {
	private ClassDescription rootDescription;
	private String[] pathExpressions;
	
public ClusterDescription() {
	super();
}

public ClusterDescription(ClassDescription rootDescription, String...pathExpressions) {
	super();
	this.rootDescription = rootDescription;
	this.pathExpressions = pathExpressions;
}
	
public ClassDescription getRootDescription() {
	return rootDescription;
}

public void setRootDescription(ClassDescription rootDescription) {
	this.rootDescription = rootDescription;
}

public String[] getPathExpressions() {
	return pathExpressions;
}

public void setPathExpressions(String[] pathExpressions) {
	this.pathExpressions = pathExpressions;
}
	
}
