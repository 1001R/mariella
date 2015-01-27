package org.mariella.persistence.oracle;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.mapping.JoinedClassMapping;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;

public class OracleSchema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	return new OracleBatchInsertStatementBuilder();
}

@Override
public SingleRowPreparedStatementBuilder createJoinedUpsertStatementBuilder(ObjectPersistor objectPersistor, JoinedClassMapping joinedClassMapping, Row row) {
	return new OracleJoinedUpsertStatementBuilder(objectPersistor, joinedClassMapping, row);
}

}
