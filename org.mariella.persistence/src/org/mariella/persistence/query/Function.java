package org.mariella.persistence.query;

import java.util.ArrayList;
import java.util.List;

public class Function implements Expression {
	private final String sqlName;
	private final List<Expression> parameters = new ArrayList<Expression>();
	
public Function(String sqlName, Expression ... parameters) {
	super();
	this.sqlName = sqlName;
	for(Expression parameter : parameters) {
		this.parameters.add(parameter);
	}
}

public String getSqlName() {
	return sqlName;
}

public List<Expression> getParameters() {
	return parameters;
}

@Override
public void printSql(StringBuilder b) {
	b.append(sqlName);
	b.append('(');
	boolean first = true;
	for(Expression parameter : parameters) {
		if(first) first = false;
		else b.append(", ");
		parameter.printSql(b);
	}
	b.append(')');
}

}
