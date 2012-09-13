package org.mariella.persistence.mapping;

import java.sql.ResultSet;

public interface ResultSetReader {
public int getCurrentColumnIndex();
public void setCurrentColumnIndex(int index);
public ResultSet getResultSet();
}
