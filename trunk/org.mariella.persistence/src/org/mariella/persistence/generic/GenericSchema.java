package org.mariella.persistence.generic;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.mapping.JoinedClassMapping;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;

public class GenericSchema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	throw new UnsupportedOperationException();
}

@Override
public SingleRowPreparedStatementBuilder createJoinedUpsertStatementBuilder(ObjectPersistor objectPersistor, JoinedClassMapping joinedClassMapping, Row row) {
	throw new UnsupportedOperationException();
}

}
