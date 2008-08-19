package at.hts.persistence.persistor;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseAccess {
public long generateId();
public Connection getConnection() throws SQLException;
}
