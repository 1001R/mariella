package at.hts.persistence.loader;

public class LoadingPolicy {
	private ClusterLoader loader;
	private String pathExpression;
	
public LoadingPolicy(ClusterLoader loader, String pathExpression) {
	super();
	this.loader = loader;
	this.pathExpression = pathExpression;
}
	
public ClusterLoader getLoader() {
	return loader;
}

public String getPathExpression() {
	return pathExpression;
}

}
