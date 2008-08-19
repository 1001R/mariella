package at.hts.persistence.query;

public interface TableReference extends Expression {
public String getAlias();
public void printExpression(StringBuilder b);
}
