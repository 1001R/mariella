package org.mariella.persistence.query;

public interface IQueryBuilderNamespace {

	
public TableReference getTableReference(String pathExpression);
public boolean hasTableReference(String pathExpression);
public void register(String pathExpression, TableReference tableReference);
}
