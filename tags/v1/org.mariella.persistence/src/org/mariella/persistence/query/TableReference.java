package org.mariella.persistence.query;

import org.mariella.persistence.database.Column;

public interface TableReference extends FromClauseElement, Expression {
public String getAlias();
public ColumnReference createColumnReference(Column column);
public ColumnReference createColumnReferenceForRelationship(Column foreignKeyColumn);
public ColumnReference createUnreferencedColumnReference(Column column);
public boolean canCreateColumnReference(Column column);
public boolean isReferenced();
}
