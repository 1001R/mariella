package org.mariella.persistence.query;

public interface QueryBuilderNamespaceProvider {
	public IQueryBuilderNamespace getNamespace(String pathExpression);
}
