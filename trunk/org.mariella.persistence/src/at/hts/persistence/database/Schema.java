package at.hts.persistence.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Schema {
	private Map<String, Table> tables = new HashMap<String, Table>();

public Collection<Table> getTables() {
	return tables.values();
}

public Table getTable(String name) {
	return tables.get(name);
}

public void addTable(Table table) {
	tables.put(table.getName(), table);
}


	
}
