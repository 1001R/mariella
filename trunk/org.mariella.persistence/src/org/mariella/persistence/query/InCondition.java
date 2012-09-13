package org.mariella.persistence.query;

import java.util.List;

public class InCondition implements Expression {
	private Expression leftExpression;
	private List<Expression> in;
	
public InCondition(Expression leftExpression, List<Expression> in) {
	super();
	this.leftExpression = leftExpression;
	this.in = in;
}
	
public void printSql(StringBuilder b) {
	leftExpression.printSql(b);
	b.append(" IN (");
	boolean first = true;
	for(Expression expression : in) {
		if(first) first = false;
		else b.append(", ");
		expression.printSql(b);
	}
	b.append(")");
}

}
