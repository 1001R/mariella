package org.mariella.persistence.query;

import org.mariella.persistence.mapping.ClassMapping;

public interface QueryBuilderListener {
	public QueryBuilderListener Default = new QueryBuilderListener() {
		public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, ClassMapping classMapping, TableReference tableReference) {
			
		}
	};
	
public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, ClassMapping classMapping, TableReference tableReference);

}
