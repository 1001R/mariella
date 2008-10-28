package org.mariella.persistence.query;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.Table;

public class SubSelectBuilder {
	private final SubSelect subSelect = new SubSelect();
	
public SubSelectBuilder() {
	super();
}

public SubSelect getSubSelect() {
	return subSelect;
}
	
public JoinedTable join(Table table) {
	JoinedTable joinedTable = new JoinedTable();
	joinedTable.setAlias(createTableAlias(table));
	joinedTable.setTable(table);
	subSelect.getFromClause().getJoinedTableReferences().add(joinedTable);
	return joinedTable;
}
	
public SelectItem addSelectItem(TableReference tableReference, Column column) {
	return addSelectItem(new ColumnReference(tableReference, column));
}

public SelectItem addSelectItem(ColumnReference cr) {
	SelectItem selectItem = new SelectItem();
	selectItem.setColumnReference(cr);
	subSelect.getSelectClause().getSelectItems().add(selectItem);
	return selectItem;
}

public Expression createCondition(final String expression) {
	return new Expression() {
		public void printSql(StringBuilder b) {
			b.append(expression);
		}
	};
}

public void and(Expression expression) {
	Expression condition = subSelect.getWhereClause().getCondition();
	if(condition == null) {
		subSelect.getWhereClause().setCondition(expression);
	} else {
		subSelect.getWhereClause().setCondition(BinaryCondition.and(condition, expression));
	}
}

public void or(Expression expression) {
	Expression condition = subSelect.getWhereClause().getCondition();
	if(condition == null) {
		subSelect.getWhereClause().setCondition(expression);
	} else {
		subSelect.getWhereClause().setCondition(BinaryCondition.or(condition, expression));
	}
}

private String createTableAlias(Table table) {
	int count = 0;
	String alias;
	boolean found;
	do {
		alias = table.getName() + count;
		found = false;
		for(TableReference tableReference : subSelect.getFromClause().getJoinedTableReferences()) {
			if(tableReference.getAlias().equals(alias)) {
				found = true;
				break;
			}
		}
		count++;
	} while(found);
	return alias;
}

}
