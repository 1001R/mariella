package org.mariella.persistence.springtest.service;

import java.util.List;

import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.persistence.loader.ClusterLoader;
import org.mariella.persistence.loader.ClusterLoaderConditionProviderImpl;
import org.mariella.persistence.loader.LoaderContext;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.query.BinaryCondition;
import org.mariella.persistence.query.QueryBuilder;
import org.mariella.persistence.query.StringLiteral;
import org.mariella.persistence.query.TableReference;

public class Test {
	protected OxyServerEntityManager entityManager;
	
public Test(OxyServerEntityManager entityManager) {
	super();
	this.entityManager = entityManager;
}

@SuppressWarnings("unchecked")
public <T> T load(ClusterDescription clusterDescription, final String id) {
	ClusterLoader loader = new ClusterLoader(entityManager.getSchemaMapping(), clusterDescription);
	LoaderContext loaderContext = new LoaderContext(entityManager.getObjectPool().getModificationTracker(), entityManager.getModifiableFactory());
	List<T> list = (List<T>)loader.load(entityManager.createDatabaseAccess(), loaderContext, new ClusterLoaderConditionProviderImpl() {
		@Override
		public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, ClassMapping classMapping, TableReference tableReference) {
			queryBuilder.and(
				BinaryCondition.eq(queryBuilder.createColumnReference("root.id"), new StringLiteral(id))
			);
		}
		
		@Override
		public String[] getConditionPathExpressions() {
			return new String[] { "root" };
		}
	});
	
	if(list.isEmpty()) {
		return null;
	} else if(list.size() == 1) {
		return list.get(0);
	} else {
		throw new IllegalStateException("Illegal result size " + list.size() + ". Expected 0 or 1");
	}
}



}
