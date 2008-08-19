package at.hts.persistence.query;

import java.util.ArrayList;
import java.util.List;

public class FromClause implements Expression {
	private List<TableReference> joinedTableReferences = new ArrayList<TableReference>();

public List<TableReference> getJoinedTableReferences() {
	return joinedTableReferences;
}

public void printSql(StringBuilder b) {
	b.append("FROM ");
	boolean first = true;
	for(TableReference tableReference : joinedTableReferences) {
		if(first) first = false;
		else b.append(", ");
		tableReference.printExpression(b);
		b.append(' ');
		tableReference.printSql(b);
	}
}

}
