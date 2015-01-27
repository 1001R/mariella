package org.mariella.persistence.h2;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.mapping.JoinedClassMapping;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;

public class H2Schema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	throw new UnsupportedOperationException();
}

@Override
public SingleRowPreparedStatementBuilder createJoinedUpsertStatementBuilder(ObjectPersistor objectPersistor, JoinedClassMapping joinedClassMapping, Row row) {
	throw new UnsupportedOperationException();
}

}
