package at.hts.persistence.query;


public class Select implements Expression {
	private Expression subSelect = new SubSelect();
	private OrderByClause orderBy = new OrderByClause();
	
public void printSql(StringBuilder b) {
	subSelect.printSql(b);
	if(!orderBy.isEmpty()) {
		b.append(' ');
		orderBy.printSql(b);
	}
}

public String toSqlString() {
	StringBuilder b  = new StringBuilder();
	printSql(b);
	return b.toString();
}

public Expression getSubSelect() {
	return subSelect;
}

public void setSubSelect(Expression subSelect) {
	this.subSelect = subSelect;
}

public OrderByClause getOrderBy() {
	return orderBy;
}

}
