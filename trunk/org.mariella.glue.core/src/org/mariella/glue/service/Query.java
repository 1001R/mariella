package org.mariella.glue.service;

import java.beans.PropertyDescriptor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.glue.core.Activator;
import org.springframework.jdbc.core.JdbcTemplate;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Converter;
import at.hts.persistence.query.BinaryCondition;
import at.hts.persistence.query.ColumnReference;
import at.hts.persistence.query.Expression;
import at.hts.persistence.query.Parameter;
import at.hts.persistence.query.QueryBuilder;
import at.hts.persistence.query.SelectItem;
import at.hts.persistence.runtime.BeanInfo;
import at.hts.persistence.runtime.Introspector;
import at.hts.persistence.schema.ClassDescription;

public abstract class Query <T, E>{
	protected final Context context;
	protected final JdbcTemplate jdbcTemplate;
	protected final ClassDescription classDescription;
	protected final QueryBuilder queryBuilder;
	protected final T searchParameter;

	protected Map<Parameter, String> parameterNames= new HashMap<Parameter, String>();
	protected Map<Parameter, Column> parameterColumns = new HashMap<Parameter, Column>();
	
	protected List<Parameter> parameters = new ArrayList<Parameter>();
	protected List<String> resultProperties = new ArrayList<String>();
	
public Query(Context context, JdbcTemplate jdbcTemplate, ClassDescription classDescription, T searchParameter) {
	super();
	this.context = context;
	this.jdbcTemplate = jdbcTemplate;
	this.classDescription = classDescription;
	this.searchParameter = searchParameter;
	queryBuilder = new QueryBuilder(context.getSchemaMapping());
	queryBuilder.join(classDescription, "root");
}

protected abstract E createResultEntry();
protected abstract void updateQueryBuilder();

public QueryResult<E> execute() {
	updateQueryBuilder();
	StringBuilder b = new StringBuilder();
	queryBuilder.getSubSelect().printSql(b);
	List<E> resultList = executeSearch(context, b.toString());
	return new QueryResult<E>(queryBuilder, resultProperties, resultList);
}

protected E createResultEntry(ResultSet rs) {
	E entry = createResultEntry();
	for(int i = 0; i < resultProperties.size(); i++) {
		String propertyName = resultProperties.get(i);
		BeanInfo beanInfo = Introspector.Singleton.getBeanInfo(entry.getClass());
		PropertyDescriptor pd = beanInfo.getPropertyDescriptor(propertyName);
		if(pd != null) {
			Expression si = queryBuilder.getSubSelect().getSelectClause().getSelectItems().get(i);
			try {
				Object value = getResultValue(rs, i + 1, si, pd);
				pd.getWriteMethod().invoke(entry, new Object[] { value });
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	return entry;
}

protected Object getResultValue(ResultSet rs, int idx, Expression selectItem, PropertyDescriptor pd) throws SQLException {
	if(selectItem instanceof SelectItem) {
		Expression columnReference = ((SelectItem)selectItem).getColumnReference();
		if(columnReference instanceof ColumnReference) {
			return ((ColumnReference)columnReference).getColumn().getObject(rs, idx);
		}
	}
	throw new IllegalArgumentException();
}

protected SelectItem addSelectItem(String propertyName, String pathExpression) {
	ColumnReference columnReference = queryBuilder.createColumnReference(pathExpression);
	resultProperties.add(propertyName);
	return queryBuilder.addSelectItem(columnReference);
}

protected BinaryCondition like(String parameterName, String pathExpression) {
	return addBinaryCondition(parameterName, pathExpression, "like");
}

protected BinaryCondition gt(String parameterName, String pathExpression) {
	return addBinaryCondition(parameterName, pathExpression, ">");
}

protected BinaryCondition gteq(String parameterName, String pathExpression) {
	return addBinaryCondition(parameterName, pathExpression, ">=");
}

protected BinaryCondition lt(String parameterName, String pathExpression) {
	return addBinaryCondition(parameterName, pathExpression, "<");
}

protected BinaryCondition lteq(String parameterName, String pathExpression) {
	return addBinaryCondition(parameterName, pathExpression, "<=");
}

protected BinaryCondition eq(String parameterName, String pathExpression) {
	return addBinaryCondition(parameterName, pathExpression, "=");
}

protected BinaryCondition neq(String parameterName, String pathExpression) {
	return addBinaryCondition(parameterName, pathExpression, "<>");
}

protected Parameter createParameter(String parameterName, Column column) {
	Parameter parameter = new Parameter();
	parameterNames.put(parameter, parameterName);
	parameterColumns.put(parameter, column);
	parameters.add(parameter);
	return parameter;
}

protected BinaryCondition addBinaryCondition(String parameterName, String pathExpression, String operator) {
	ColumnReference columnReference = queryBuilder.createColumnReference(pathExpression);
	Parameter parameter = createParameter(parameterName, columnReference.getColumn());
	
	BinaryCondition binaryCondition = new BinaryCondition();
	binaryCondition.setLeft(columnReference);
	binaryCondition.setRight(parameter);
	binaryCondition.setOperator(operator);

	queryBuilder.and(binaryCondition);
	return binaryCondition;
}

protected void setParameters(PreparedStatement ps) {
	int idx = 1;
	for(Parameter parameter : parameters) {
		String parameterName = parameterNames.get(parameter);
		Column column = parameterColumns.get(parameter);
		setParameter(ps, column, parameterName, idx);
		idx++;
	}
}

@SuppressWarnings("unchecked")
protected void setParameter(PreparedStatement preparedStatement, Column column, String parameterName, int idx) {
	BeanInfo beanInfo = Introspector.Singleton.getBeanInfo(searchParameter.getClass());
	PropertyDescriptor pd = beanInfo.getPropertyDescriptor(parameterName);
	try {
		Object value = pd.getReadMethod().invoke(searchParameter, new Object[] {});
		((Converter)column.getConverter()).setObject(preparedStatement, idx, column.getType(), value);
	} catch(Exception e) {
		throw new RuntimeException(e);
	}
}

protected List<E> executeSearch(Context context, final String sql) {
	TransactionalRunner tr = new TransactionalRunner(jdbcTemplate.getDataSource());
	return tr.run(
		new TransactionalRunnable<List<E>>() {
			public List<E> run(TransactionalRunner runner) throws SQLException {
				Activator.logger.info(sql);
				PreparedStatement ps = runner.getConnection().prepareStatement(sql);
				setParameters(ps);
				try {
					ResultSet rs = ps.executeQuery();
					try {
						List<E> result = new ArrayList<E>();
						while(rs.next()) {
							E er = createResultEntry(rs);
							result.add(er);
						}
						return result;
					} finally {
						rs.close();
					}
				} finally {
					ps.close();
				}
			}
		}
	);
}

}
