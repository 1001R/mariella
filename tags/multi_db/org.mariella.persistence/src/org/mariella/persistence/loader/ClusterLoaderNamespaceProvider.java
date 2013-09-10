package org.mariella.persistence.loader;

import org.mariella.persistence.query.IQueryBuilderNamespace;
import org.mariella.persistence.query.QueryBuilderNamespace;
import org.mariella.persistence.query.QueryBuilderNamespaceProvider;

public class ClusterLoaderNamespaceProvider implements QueryBuilderNamespaceProvider {
	public enum Mode {
		query,
		conditions;
		
		public IQueryBuilderNamespace namespace = new QueryBuilderNamespace();
	};
	
	private IQueryBuilderNamespace global = new QueryBuilderNamespace();
	
	private Mode mode = Mode.query;
	
public void setMode(Mode mode) {
	this.mode = mode;
}

public Mode getMode() {
	return mode;
}
	
@Override
public IQueryBuilderNamespace getNamespace(String pathExpression) {
	if(pathExpression.indexOf('.') == -1) {
		return global;
	} else {
		return mode.namespace;
	}
}

}
