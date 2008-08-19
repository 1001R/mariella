package at.hts.persistence.loader;

import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.query.QueryBuilder;
import at.hts.persistence.query.QueryBuilderListener;
import at.hts.persistence.query.QueryBuilderNamespace;
import at.hts.persistence.query.QueryBuilderNamespaceProvider;
import at.hts.persistence.query.TableReference;

public class LoadingPolicyStatementBuilder {
	
public static class NamespaceProvider implements QueryBuilderNamespaceProvider {
	public enum Mode {
		query,
		conditions
	};
	
	private QueryBuilderNamespace nsGlobal = new QueryBuilderNamespace();
	private QueryBuilderNamespace nsQuery = new QueryBuilderNamespace();
	private QueryBuilderNamespace nsConditions = new QueryBuilderNamespace();
	
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
		return nsGlobal;
	} else {
		return mode == Mode.query ? nsQuery : nsConditions;
	}
}

};

	private final LoadingPolicy loadingPolicy;
	private final ClusterLoaderConditionProvider conditionProvider;
	
public LoadingPolicyStatementBuilder(LoadingPolicy loadingPolicy, ClusterLoaderConditionProvider conditionProvider) {
	super();
	this.loadingPolicy = loadingPolicy;
	this.conditionProvider = conditionProvider;
}

public String createSelectStatement() {
	NamespaceProvider namespaceProvider = new NamespaceProvider();
	final QueryBuilder queryBuilder = new QueryBuilder(loadingPolicy.getLoader().getSchemaMapping(), namespaceProvider);
	
	QueryBuilderListener selectItemListener = new QueryBuilderListener() {
			public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, ClassMapping classMapping, TableReference tableReference) {
				if(pathExpression.length() == loadingPolicy.getPathExpression().length()) {
					classMapping.addObjectColumns(queryBuilder, tableReference);
				} else {
					classMapping.addIdentityColumns(queryBuilder, tableReference);
				}
			}
		};

	queryBuilder.addListener(selectItemListener);
	queryBuilder.join(loadingPolicy.getLoader().getClusterDescription().getRootDescription(), "root");
	queryBuilder.join(loadingPolicy.getPathExpression());
	queryBuilder.removeListener(selectItemListener);
	
	namespaceProvider.setMode(NamespaceProvider.Mode.conditions);
	queryBuilder.addListener(conditionProvider);
	for(String conditionPathExpression : conditionProvider.getConditionPathExpressions()) {
		queryBuilder.join(conditionPathExpression);
	}
	
	StringBuilder b = new StringBuilder();
	queryBuilder.getSubSelect().printSql(b);
	return b.toString();
}

}
