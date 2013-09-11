package org.mariella.persistence.generic;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;

public class GenericSchema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	throw new UnsupportedOperationException();
}


}
