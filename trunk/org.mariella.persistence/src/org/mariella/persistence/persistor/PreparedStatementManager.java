package org.mariella.persistence.persistor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PreparedStatementManager {

	private static class PreparedStatementEntry {
		private String table;
		private PreparedStatement preparedStatement;
		
		public PreparedStatementEntry(String table, PreparedStatement preparedStatement) {
			this.table = table;
			this.preparedStatement = preparedStatement;
		}
	}
	
	private Connection connection;
	private List<String> orderedBatchedTables;
	private List<PreparedStatementEntry> preparedStatements = new ArrayList<PreparedStatementEntry>();
	private Map<String, PreparedStatement> preparedStatementMap = new HashMap<String, PreparedStatement>();
	private int rowsEntered = 0;
	
	public PreparedStatementManager(Connection connection, List<String> tableDependenyOrder) {
		this.connection = connection;
		this.orderedBatchedTables = tableDependenyOrder;
	}
	
	public PreparedStatement prepareStatement(String table, String sql) throws SQLException {
		if (orderedBatchedTables.contains(table)) {
			PreparedStatement ps = preparedStatementMap.get(sql);
			if (ps == null) {
				ps = connection.prepareStatement(sql);
				preparedStatementMap.put(sql, ps);
				preparedStatements.add(new PreparedStatementEntry(table, ps));
			}
			return ps;
		} else {
			return connection.prepareStatement(sql);
		}
	}
	
	public void prepared(PreparedStatement ps) throws SQLException {
		if (preparedStatementMap.values().contains(ps)) {
			ps.addBatch();
			rowsEntered++;
		} else {
			try {
				ps.executeUpdate();
			} finally {
				ps.close();
			}
		}
	}
	
	public void executeAll() throws SQLException {
		if (orderedBatchedTables != null && !orderedBatchedTables.isEmpty() && !preparedStatements.isEmpty()) {
			Collections.sort(preparedStatements, new Comparator<PreparedStatementEntry>() {
				@Override
				public int compare(PreparedStatementEntry o1, PreparedStatementEntry o2) {
					int i1 = orderedBatchedTables.indexOf(o1.table);
					int i2 = orderedBatchedTables.indexOf(o2.table);
					return i1-i2; 
				}
			});
			Iterator<PreparedStatementEntry> psIt = preparedStatements.iterator();
			try {
				while (psIt.hasNext()) {
					PreparedStatement ps = psIt.next().preparedStatement;
					ps.executeBatch();
					psIt.remove();
					ps.close();
				}
			} catch (SQLException ex) {
				while (psIt.hasNext()) {
					PreparedStatement ps = psIt.next().preparedStatement;
					try {
						ps.close();
					} catch (SQLException ex2) {
						// ignore
					}
				}
				throw ex;
			} finally {
				preparedStatementMap.clear();
				preparedStatements.clear();
				rowsEntered = 0;
			}
		}
	}
	
	public void executeAllIfMaxEntriesReached() throws SQLException {
		if (rowsEntered > 5000) {
			executeAll();
		}
	}
	
	public void cloaseAll() throws SQLException {
		SQLException ex = null;
		Iterator<PreparedStatementEntry> psIt = preparedStatements.iterator();
		while (psIt.hasNext()) {
			PreparedStatement ps = psIt.next().preparedStatement;
			try {
				ps.close();
			} catch (SQLException ex2) {
				if (ex == null) {
					ex = ex2;		// remember the first exception
				}
			}
		}
		preparedStatementMap.clear();
		preparedStatements.clear();
		rowsEntered = 0;
		if (ex != null) {
			throw ex;
		}
	}

	public PreparedStatement prepareSingleStatement(String sql, String[] columnNames) throws SQLException {
		return connection.prepareStatement(sql);
	}

}
