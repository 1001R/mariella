package org.mariella.oxygen.runtime.core;

import java.sql.Connection;

/**
 * @author aim
 */
public interface OxyConnectionProvider {
public Connection getConnection();
public void close();
}
