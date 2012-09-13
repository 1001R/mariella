package org.mariella.persistence.loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.query.BinaryCondition;
import org.mariella.persistence.query.ColumnReference;
import org.mariella.persistence.query.Expression;
import org.mariella.persistence.query.QueryBuilder;
import org.mariella.persistence.query.TableReference;


public class ClusterLoader {
	private ClusterDescription clusterDescription;
	private SchemaMapping schemaMapping;
	
	private List<LoadingPolicy> loadingPolicies = new ArrayList<LoadingPolicy>();
	
public ClusterLoader(SchemaMapping schemaMapping, ClusterDescription clusterDescription) {
	super();
	this.schemaMapping = schemaMapping;
	this.clusterDescription = clusterDescription;
	
	for(String pathExpression : clusterDescription.getPathExpressions()) {
		LoadingPolicy policy = new LoadingPolicy(this, pathExpression);
		loadingPolicies.add(policy);
	}
}
	
public ClusterDescription getClusterDescription() {
	return clusterDescription;
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

public void setSchemaMapping(SchemaMapping schemaMapping) {
	this.schemaMapping = schemaMapping;
}

public List<LoadingPolicy> getLoadingPolicies() {
	return loadingPolicies;
}

public void setLoadingPolicies(List<LoadingPolicy> loadingPolicies) {
	this.loadingPolicies = loadingPolicies;
}

public Object load(Connection connection, LoaderContext loaderContext, final Object id) {
	List<?> result = load(
		connection,
		loaderContext,
		new ClusterLoaderConditionProvider() {
			@Override
			public String[] getConditionPathExpressions() {
				return new String[] { "root" };
			}
			
			public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, final ClassMapping classMapping, TableReference tableReference) {
				if(pathExpression.equals("root")) {
					Expression condition = BinaryCondition.eq(
							new ColumnReference(tableReference, classMapping.getIdMapping().getColumn()),
							new Expression() {
								public void printSql(StringBuilder b) {
									classMapping.getIdMapping().getColumn().getConverter().createLiteral(id).printSql(b);
								};
							}
					);
					queryBuilder.and(condition);
				}
			}
		}
	);
	return result.size() == 0 ? null : result.get(0);
}

public List<?> load(Connection connection, LoaderContext loaderContext, ClusterLoaderConditionProvider conditionProvider) {
	loaderContext.getLogger().info("loading cluster");
	long ms = System.currentTimeMillis();
	loaderContext.startLoading();
	try {
		List<Object> result = new ArrayList<Object>();
	
		for(LoadingPolicy lp : loadingPolicies) {
			LoadingPolicyStatementBuilder statementBuilder = new LoadingPolicyStatementBuilder(lp, conditionProvider);
			String sql = statementBuilder.createSelectStatement();
			try {
				long psms = System.currentTimeMillis();
				PreparedStatement ps = connection.prepareStatement(sql);
				try {
					loaderContext.getLogger().info(sql);
					ResultSet rs = ps.executeQuery();
					try {
						LoadingPolicyObjectBuilder lpb = new LoadingPolicyObjectBuilder(lp, loaderContext, rs);
						for(Object m : lpb.createObjects()) {
							if(!result.contains(m)) {
								result.add(m);
							}
						}
					} finally {
						rs.close();
					}
				} finally {
					ps.close();
					loaderContext.getLogger().info("executed statement in " + (System.currentTimeMillis() - psms) + " ms");
				}
			} catch(SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	} finally {
		loaderContext.finishedLoading();
		loaderContext.getLogger().info("loaded cluster in " + (System.currentTimeMillis() - ms) + " ms");
	}
}


}
