package org.mariella.glue.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariella.glue.core.Activator;
import org.mariella.persistence.query.QueryBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;


public class QueryBuilderExecutor {
	public static interface ResultSetExtractor {
		public boolean extractData(ResultSet rs) throws SQLException;
	}
	
	public static class ResultSetCounter implements ResultSetExtractor {
		public ResultSetCounter() {
			super();
			maxCount = -1;
		}
		
		public ResultSetCounter(int maxCount) {
			super();
			this.maxCount = maxCount;
		}
		
		int count = 0;
		int maxCount;
		
		@Override
		public boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
			count++;
			return maxCount == -1 || count <= maxCount;
		}
		
		public int getCount() {
			return count;
		}
	}

	public static class LongExtractor implements ResultSetExtractor {
		private Long value = null;
		
		public LongExtractor() {
			super();
		}
		
		@Override
		public boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
			value = resultSet.getLong(1);
			return false;
		}
		
		public Long getValue() {
			return value;
		}
		
		public boolean isNull() {
			return value == null;
		}
	}

	private final QueryBuilder queryBuilder;

public QueryBuilderExecutor(QueryBuilder queryBuilder) {
	super();
	this.queryBuilder = queryBuilder;
}

public void execute(JdbcTemplate jdbcTemplate, final ResultSetExtractor resultSetExtractor) {
	execute(
		jdbcTemplate,
		new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
			}
		},
		resultSetExtractor
	);
}

public void execute(JdbcTemplate jdbcTemplate, final PreparedStatementSetter preparedStatementSetter, final ResultSetExtractor resultSetExtractor) {
	TransactionalRunner tr = new TransactionalRunner(jdbcTemplate.getDataSource());
	tr.run(
		new TransactionalRunnable<Object>() {
			public Object run(TransactionalRunner runner) throws SQLException {
				StringBuilder b = new StringBuilder();
				queryBuilder.getSubSelect().printSql(b);
				String sql = b.toString();
				
				Activator.logger.info(sql);
				PreparedStatement ps = runner.getConnection().prepareStatement(sql);
				try {
					ResultSet rs = ps.executeQuery();
					try {
						while(rs.next()) {
							if(!resultSetExtractor.extractData(rs)) {
								break;
							}
						}
						return null;
					} finally {
						rs.close();
					}
				} finally {
					ps.close();
				}
			}
		}
	);
}

}
