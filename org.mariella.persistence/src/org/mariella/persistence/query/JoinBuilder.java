package org.mariella.persistence.query;

import java.util.List;

import org.mariella.persistence.database.Column;

public interface JoinBuilder {
	public static enum JoinType {
		inner,
		leftouter,
		rightouter
	}

public JoinType getJoinType();
public void setJoinType(JoinType joinType);
public void createJoin();
public TableReference getJoinedTableReference();
public ConditionBuilder getConditionBuilder(Column column);
public List<Expression> getOrderBy();
public boolean isAddToOrderBy();
public void setAddToOrderBy(boolean addToOrderBy);
}
