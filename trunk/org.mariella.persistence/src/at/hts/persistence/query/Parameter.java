package at.hts.persistence.query;

public class Parameter implements Expression {
	
public void printSql(StringBuilder b) {
	b.append("?");
}

}
