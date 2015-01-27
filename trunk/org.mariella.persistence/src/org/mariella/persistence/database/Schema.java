package org.mariella.persistence.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mariella.persistence.mapping.JoinedClassMapping;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;

public abstract class Schema {
	private Map<String, Table> tables = new HashMap<String, Table>();
	private Map<String, Sequence> sequences = new HashMap<String, Sequence>();
	
public abstract BatchInsertStatementBuilder createBatchInsertStatementBuilder();
public abstract SingleRowPreparedStatementBuilder createJoinedUpsertStatementBuilder(ObjectPersistor objectPersistor, JoinedClassMapping joinedClassMapping, Row row);
	
public Collection<Table> getTables() {
	return tables.values();
}

public Table getTable(String name) {
	return tables.get(name);
}

public void addTable(Table table) {
	tables.put(table.getName(), table);
}

public Collection<Sequence> getSequences() {
	return sequences.values();
}

public Sequence getSequence(String name) {
	return sequences.get(name);
}

public void addSequence(Sequence sequence) {
	sequences.put(sequence.getName(), sequence);
}

}
