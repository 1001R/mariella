package at.hts.persistence.query;

public interface QueryBuilderNamespaceProvider {
	public QueryBuilderNamespaceProvider Default = new QueryBuilderNamespaceProvider() {
		private QueryBuilderNamespace namespace = new QueryBuilderNamespace();
		
		public QueryBuilderNamespace getNamespace(String pathExpression) {
			return namespace;
		}
	};
	
	public QueryBuilderNamespace getNamespace(String pathExpression);
}
