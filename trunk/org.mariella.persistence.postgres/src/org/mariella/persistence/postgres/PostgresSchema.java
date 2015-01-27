package org.mariella.persistence.postgres;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.mapping.JoinedClassMapping;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;

public class PostgresSchema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	return new PostgresBatchInsertStatementBuilder();
}

@Override
public SingleRowPreparedStatementBuilder createJoinedUpsertStatementBuilder(ObjectPersistor objectPersistor, JoinedClassMapping joinedClassMapping, Row row) {
	return new PostgresJoinedUpsertStatementBuilder(objectPersistor, joinedClassMapping, row);
}

}
