package at.hts.persistence.query;


import at.hts.persistence.database.Table;

public class JoinedTable implements TableReference {
	private String alias;
	private Table table;
	
public String getAlias() {
	return alias;
}

public void setAlias(String identifier) {
	this.alias = identifier;
}

public Table getTable() {
	return table;
}

public void setTable(Table table) {
	this.table = table;
}

public void printSql(StringBuilder b) {
	if(alias != null) {
		b.append(alias);
	} else {
		b.append(table.getName());
	}
}

public void printExpression(StringBuilder b) {
	b.append(table.getName());
}

}
