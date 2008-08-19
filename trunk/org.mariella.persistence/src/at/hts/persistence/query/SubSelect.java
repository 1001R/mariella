package at.hts.persistence.query;



public class SubSelect implements Expression {
	private SelectClause selectClause = new SelectClause();
	private FromClause fromClause = new FromClause();
	private WhereClause whereClause = new WhereClause();
	public SelectClause getSelectClause() {
	return selectClause;
}

public FromClause getFromClause() {
	return fromClause;
}

public WhereClause getWhereClause() {
	return whereClause;
}

public void printSql(StringBuilder b) {
	selectClause.printSql(b);
	b.append(' ');
	fromClause.printSql(b);
	if(!whereClause.isEmpty()) {
		b.append(' ');
		whereClause.printSql(b);
	}
}

public String toSqlString() {
	StringBuilder b  = new StringBuilder();
	printSql(b);
	return b.toString();
}

}
