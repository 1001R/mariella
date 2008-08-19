package at.hts.persistence.query;

import java.util.ArrayList;
import java.util.List;

public class OrderByClause implements Expression {
	private List<Expression> items = new ArrayList<Expression>();
	
public boolean isEmpty() {
	return items.isEmpty();
}
	
public List<Expression> getItems() {
	return items;
}

public void printSql(StringBuilder b) {
	b.append("ORDER BY ");
	boolean first = true;
	for(Expression item : items) {
		if(first) first = false;
		else b.append(", ");
		item.printSql(b);
	}
}

}
