package org.mariella.persistence.loader;

import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.query.QueryBuilder;
import org.mariella.persistence.query.QueryBuilderListener;
import org.mariella.persistence.query.TableReference;


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
