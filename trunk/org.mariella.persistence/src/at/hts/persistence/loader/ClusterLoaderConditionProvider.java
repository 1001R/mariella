package at.hts.persistence.loader;

import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.query.QueryBuilder;
import at.hts.persistence.query.QueryBuilderListener;
import at.hts.persistence.query.TableReference;

public interface ClusterLoaderConditionProvider extends QueryBuilderListener {
	public static ClusterLoaderConditionProvider Default = new ClusterLoaderConditionProvider() {
		@Override
		public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, ClassMapping classMapping, TableReference tableReference) {
		}
	
		@Override
		public String[] getConditionPathExpressions() {
			return new String[] {};
		}
	};
	
public String[] getConditionPathExpressions();
	
}
