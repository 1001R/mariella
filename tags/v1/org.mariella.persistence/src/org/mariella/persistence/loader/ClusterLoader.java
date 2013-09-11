package org.mariella.persistence.loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.ConnectionCallback;
import org.mariella.persistence.database.Converter;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.ColumnMapping;
import org.mariella.persistence.mapping.RelationshipPropertyMapping;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.persistor.DatabaseAccess;
import org.mariella.persistence.query.BinaryCondition;
import org.mariella.persistence.query.Expression;
import org.mariella.persistence.query.JoinBuilder;
import org.mariella.persistence.query.Parameter;
import org.mariella.persistence.query.QueryBuilder;
import org.mariella.persistence.query.TableReference;


public class ClusterLoader {
	private class ClusterLoaderQueryParameter {
		private final Converter<?> converter;
		private final int sqlType;
		private final Object value;
		private final int index;

		public ClusterLoaderQueryParameter(Column column, Object value, int index) {
			this.converter = column.getConverter();
			this.sqlType = column.getType();
			this.value = value;
			this.index = index;
		}

		public ClusterLoaderQueryParameter(Converter<?> converter, int sqlType, Object value, int index) {
			this.converter = converter;
			this.sqlType = sqlType;
			this.value = value;
			this.index = index;
		}

		@SuppressWarnings("unchecked")
		public void setParameter(PreparedStatement ps) throws SQLException {
			((Converter)converter).setObject(ps, index, sqlType, value);
		}
	}


	private ClusterDescription clusterDescription;
	private SchemaMapping schemaMapping;

	private List<LoadingPolicy> loadingPolicies = new ArrayList<LoadingPolicy>();
	private List<ClusterLoaderQueryParameter> queryParameters = new ArrayList<ClusterLoaderQueryParameter>();

public ClusterLoader(SchemaMapping schemaMapping, ClusterDescription clusterDescription) {
	super();
	this.schemaMapping = schemaMapping;
	this.clusterDescription = clusterDescription;

	if(clusterDescription.getPathExpressions().length == 0 || !clusterDescription.getPathExpressions()[0].equals("root")) {
		addLoadingPolicy("root");
	}
	for(String pathExpression : clusterDescription.getPathExpressions()) {
		addLoadingPolicy(pathExpression);
	}
}

private void addLoadingPolicy(String pathExpression) {
	LoadingPolicy policy = new LoadingPolicy(this, pathExpression);
	policy.setPropertyChooser(clusterDescription.getPropertyChooser(pathExpression));
	loadingPolicies.add(policy);
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

public Object load(DatabaseAccess databaseAccess, LoaderContext loaderContext, final Object id) {
	List<?> result = load(
		databaseAccess,
		loaderContext,
		new ClusterLoaderConditionProvider() {
			@Override
			public String[] getConditionPathExpressions() {
				return new String[] { "root" };
			}

			@Override
			public void aboutToJoinRelationship(QueryBuilder queryBuilder, String pathExpression, RelationshipPropertyMapping rpm, JoinBuilder joinBuilder) {
			}

			public void pathExpressionJoined(QueryBuilder queryBuilder, String pathExpression, final ClassMapping classMapping, TableReference tableReference) {
				if(pathExpression.equals("root")) {
					for(final ColumnMapping columnMapping : classMapping.getPrimaryKey().getColumnMappings()) {
						Expression condition = BinaryCondition.eq(
							tableReference.createColumnReference(columnMapping.getReadColumn()),
							new Parameter()
						);
						queryBuilder.and(condition);
						addParameter(columnMapping.getReadColumn(), id);
					}
				}
			}
		}
	);
	return result.size() == 0 ? null : result.get(0);
}

public void addParameter(Column column, Object value) {
	int index = queryParameters.size() + 1;
	queryParameters.add(new ClusterLoaderQueryParameter(column, value, index));
}

public void addParameter(Converter<?> converter, int sqlType, Object value) {
	int index = queryParameters.size() + 1;
	queryParameters.add(new ClusterLoaderQueryParameter(converter, sqlType, value, index));
}

private void clearParameters() {
	queryParameters = new ArrayList<ClusterLoaderQueryParameter>();
}

private void setParameters(PreparedStatement ps) throws SQLException {
	for(ClusterLoaderQueryParameter parameter : queryParameters) {
		parameter.setParameter(ps);
	}
}

public List<?> load(DatabaseAccess databaseAccess, final LoaderContext loaderContext, final ClusterLoaderConditionProvider conditionProvider) {
	try {
		return (List<?>)databaseAccess.doInConnection(
			new ConnectionCallback() {
				@Override
				public Object doInConnection(Connection connection)	throws SQLException {
					loaderContext.getLogger().info("loading cluster");
					long ms = System.currentTimeMillis();
					loaderContext.startLoading();
					try {
						List<Object> result = new ArrayList<Object>();

						for(LoadingPolicy lp : loadingPolicies) {
							clearParameters();
							LoadingPolicyStatementBuilder statementBuilder = new LoadingPolicyStatementBuilder(lp, loaderContext, conditionProvider);
							String sql = statementBuilder.createSelectStatement();
							long psms = System.currentTimeMillis();
							PreparedStatement ps = connection.prepareStatement(sql);
							setParameters(ps);
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
						}
						return result;
					} finally {
						loaderContext.finishedLoading();
						loaderContext.getLogger().info("loaded cluster in " + (System.currentTimeMillis() - ms) + " ms");
					}
				}
			}
		);
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}
}


}
