package org.mariella.persistence.persistor;

import java.sql.Connection;

public interface TransactionalRunner {
public void run(Connection connection);
}
