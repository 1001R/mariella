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

import org.mariella.persistence.runtime.MariellaPersistence;

public class PersistenceStatementsManager {

	private enum BatchMode {
		none,
		batched,
		forcedBatched
	}
	
	public static class PersistenceStatement {
		private String sql;
		private int batchSize = 0;
		private String table;
		private PreparedStatement preparedStatement;
		private boolean delete;
		private BatchMode batchMode;
		
		private PersistenceStatement(String table, boolean delete, BatchMode batchMode, PreparedStatement preparedStatement, String sql) {
			this.table = table;
			this.delete = delete;
			this.batchMode = batchMode;
			this.preparedStatement = preparedStatement;
			this.sql = sql;
		}
		
		public PreparedStatement getPreparedStatement() {
			return preparedStatement;
		}
		
		public void execute(String sqlDebugString) throws SQLException {
			if (batchMode == BatchMode.none) {
				if (sqlDebugString != null) {
					MariellaPersistence.logger.info(sqlDebugString);
				}
				try {
					preparedStatement.executeUpdate();
				} finally {
					preparedStatement.close();
				}
			} else {
				preparedStatement.addBatch();
				batchSize++;
			}
		}
		
		public void executeBatch() throws SQLException {
			if (batchMode == BatchMode.batched) {
				MariellaPersistence.logger.info("Batched over "+batchSize+" rows: "+sql);
				preparedStatement.executeBatch();
				batchSize = 0;
			}
		}

		public void close() throws SQLException {
			if (batchMode == BatchMode.batched) {
				preparedStatement.close();
			}
		}
	}
	
	private Connection connection;
	private int maxBatchSize;
	private List<String> orderedBatchedTables;
	private List<PersistenceStatement> preparedStatements = new ArrayList<PersistenceStatement>();
	private Map<String, PersistenceStatement> preparedStatementMap = new HashMap<String, PersistenceStatement>();
	
	public PersistenceStatementsManager(Connection connection, int maxBatchSize, List<String> tableDependenyOrder) {
		this.connection = connection;
		this.maxBatchSize = maxBatchSize;
		this.orderedBatchedTables = tableDependenyOrder;
	}
	
	private PersistenceStatement prepareStatement(String table, boolean delete, boolean batched, String sql) throws SQLException {
		if (orderedBatchedTables.contains(table)) {
			PersistenceStatement mps = preparedStatementMap.get(sql);
			if (mps == null) {
				mps = new PersistenceStatement(table, delete, BatchMode.forcedBatched, connection.prepareStatement(sql), sql);
				preparedStatementMap.put(sql, mps);
				preparedStatements.add(mps);
			}
			return mps;
		} else {
			return new PersistenceStatement(table, delete, batched ? BatchMode.batched : BatchMode.none, connection.prepareStatement(sql), sql);
		}
	}
	
	public PersistenceStatement prepareBatchedStatement(String table, boolean delete, String sql) throws SQLException {
		return prepareStatement(table, delete, true, sql);
	}
	
	public PersistenceStatement prepareStatement(String table, boolean delete, String sql) throws SQLException {
		return prepareStatement(table, delete, false, sql);
	}

	
	public void executeAll() throws SQLException {
		if (orderedBatchedTables != null && !orderedBatchedTables.isEmpty() && !preparedStatements.isEmpty()) {
			Collections.sort(preparedStatements, new Comparator<PersistenceStatement>() {
				@Override
				public int compare(PersistenceStatement o1, PersistenceStatement o2) {
					if (o1.delete != o2.delete) {
						// execute deletes after insert/updates
						return o1.delete ? 1 : -1;
					} else {
						int i1 = orderedBatchedTables.indexOf(o1.table);
						int i2 = orderedBatchedTables.indexOf(o2.table);
						// deletes are executed in the reverse order
						return o1.delete ? i2-i1 : i1-i2;
					}
				}
			});
			Iterator<PersistenceStatement> psIt = preparedStatements.iterator();
			try {
				while (psIt.hasNext()) {
					PersistenceStatement psEntry = psIt.next();
					MariellaPersistence.logger.info("Batched over "+psEntry.batchSize+" rows: "+psEntry.sql);
					psEntry.preparedStatement.executeBatch();
					psIt.remove();
					psEntry.preparedStatement.close();
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
			}
		}
	}
	
	public void executeBatchedIfMaxEntriesReached() throws SQLException {
		int rowsEntered = 0;
		for (PersistenceStatement mps : preparedStatements) {
			rowsEntered += mps.batchSize;
		}
		if (rowsEntered > maxBatchSize) {
			executeAll();
		}
	}
	
	public void cloaseAll() throws SQLException {
		SQLException ex = null;
		Iterator<PersistenceStatement> psIt = preparedStatements.iterator();
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
		if (ex != null) {
			throw ex;
		}
	}

	public PreparedStatement prepareSingleStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	public PreparedStatement prepareSingleStatement(String sql, String[] columnNames) throws SQLException {
		return connection.prepareStatement(sql, columnNames);
	}

}
