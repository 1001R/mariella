package org.mariella.persistence.oracle;

import org.mariella.persistence.database.BatchInsertStatementBuilder;
import org.mariella.persistence.database.Schema;

public class OracleSchema extends Schema {

@Override
public BatchInsertStatementBuilder createBatchInsertStatementBuilder() {
	return new OracleBatchInsertStatementBuilder();
}

}
