package org.mariella.glue.service;

import java.sql.SQLException;

public interface TransactionalRunnable<T> {

public T run(TransactionalRunner runner) throws SQLException;
}
