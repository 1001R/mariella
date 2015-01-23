package org.mariella.persistence.postgres;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;

public class PostgresSchema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	return new PostgresBatchInsertStatementBuilder();
}

}
