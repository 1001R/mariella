package at.hts.persistence.loader;

import at.hts.persistence.query.QueryBuilderNamespace;
import at.hts.persistence.query.QueryBuilderNamespaceProvider;

public class ClusterLoaderNamespaceProvider implements QueryBuilderNamespaceProvider {
	public enum Mode {
		query,
		conditions;
		
		public QueryBuilderNamespace namespace = new QueryBuilderNamespace();
	};
	
	private QueryBuilderNamespace global = new QueryBuilderNamespace();
	
	private Mode mode = Mode.query;
	
public void setMode(Mode mode) {
	this.mode = mode;
}

public Mode getMode() {
	return mode;
}
	
@Override
public QueryBuilderNamespace getNamespace(String pathExpression) {
	if(pathExpression.indexOf('.') == -1) {
		return global;
	} else {
		return mode.namespace;
	}
}

}
