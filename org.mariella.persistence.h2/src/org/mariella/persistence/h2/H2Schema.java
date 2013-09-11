package org.mariella.persistence.h2;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;

public class H2Schema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	throw new UnsupportedOperationException();
}

}
