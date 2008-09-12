package at.hts.persistence.query;

public interface QueryBuilderNamespaceProvider {
	public QueryBuilderNamespace getNamespace(String pathExpression);
}
