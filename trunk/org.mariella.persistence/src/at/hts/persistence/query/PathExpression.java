package at.hts.persistence.query;

import java.util.StringTokenizer;

import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.RelationshipPropertyDescription;

public class PathExpression {
	private String expression;
	
public PathExpression(String expression) {
	super();
	this.expression = expression;
}

@Override
public int hashCode() {
	return expression.hashCode();
}

@Override
public boolean equals(Object obj) {
	return obj instanceof PathExpression && expression.equals(((PathExpression)obj).expression);
}

public String toString() {
	return expression;
}

public void visit(PathExpressionVisitor visitor) {
	ClassDescription currentClassDescription = null;
	PropertyDescription currentPropertyDescription = null;
	StringTokenizer tokenizer = new StringTokenizer(expression, ".");
	while(tokenizer.hasMoreTokens()) {
		String token = tokenizer.nextToken();
		if(currentClassDescription == null) {
			if(currentPropertyDescription != null) {
				visitor.afterEnd(token);
			} else {
				currentClassDescription = visitor.root(token);
			}
		} else {
			currentPropertyDescription = currentClassDescription.getPropertyDescription(token);
			if(currentPropertyDescription == null) {
				visitor.unknownProperty(currentClassDescription, token);
				break;
			}
			visitor.property(currentClassDescription, currentPropertyDescription);
			if(currentPropertyDescription instanceof RelationshipPropertyDescription) {
				currentClassDescription = ((RelationshipPropertyDescription)currentPropertyDescription).getReferencedClassDescription();
			} else {
				currentClassDescription = null;
			}
		}
	}
}

}
