package org.mariella.persistence.query;

import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.RelationshipPropertyMapping;

public interface QueryBuilderListener {
	public QueryBuilderListener Default = new QueryBuilderListenerImpl();
	
public void aboutToJoinRelationship(QueryBuilder queryBuilder, String pathExpression, RelationshipPropertyMapping rpm, JoinBuilder joinBuilder);
public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, ClassMapping classMapping, TableReference tableReference);

}
