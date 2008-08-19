package at.hts.persistence.query;

public class Brackets implements Expression {
	private Expression expression;
	
public Brackets(Expression expression) {
	super();
	this.expression = expression;
}

public void printSql(StringBuilder b) {
	b.append("(");
	expression.printSql(b);
	b.append(")");
}

}
