package org.mariella.persistence.query;

public interface QueryBuilderNamespaceProvider {
	public QueryBuilderNamespace getNamespace(String pathExpression);
}
