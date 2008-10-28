package org.mariella.persistence.query;

import java.util.ArrayList;
import java.util.List;

public class SelectClause implements Expression {
	private boolean distinct = false;
	private List<Expression> selectItems = new ArrayList<Expression>();
	
public boolean isDistinct() {
	return distinct;
}

public void setDistinct(boolean distinct) {
	this.distinct = distinct;
}

public List<Expression> getSelectItems() {
	return selectItems;
}

public void setSelectItems(List<Expression> selectItems) {
	this.selectItems = selectItems;
}

public void printSql(StringBuilder b) {
	b.append("SELECT ");
	boolean first = true;
	for(Expression selectItem : selectItems) {
		if(first) first = false;
		else b.append(", ");
		selectItem.printSql(b);
	}
}

}
