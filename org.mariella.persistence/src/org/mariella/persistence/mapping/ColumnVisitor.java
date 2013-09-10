package org.mariella.persistence.mapping;

import org.mariella.persistence.database.Column;


public interface ColumnVisitor {
public void visit(Column column);
}
